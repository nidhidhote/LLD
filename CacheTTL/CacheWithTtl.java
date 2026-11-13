//package cache.v2;


import java.time.Duration;
import java.util.*;

/**
 * Changes that are required for this version:
 *
 * - We also need to store the expiry timestamp so instead of V value, we store Entry<V value, Instant expiresAt>
 * - Added a new put(key, value, ttl) method
 *
 *
 * - Introduce Clock interface, SystemClock for real implementations and FakeClock for tests
 *
 * Key parts of expiry logic:
 * - Lazy Expiry: on doing get(key), if the key is expired it is evicted, and we return as if it didn't exist
 * - Active Expiry: A background thread that will keep finding the expired keys and delete them
 * - TreeSet: An ordered set of keys based on the expiry timestamp so that you can delete the expired keys
 * (This is useful in 2 cases:
 *      1. The Background Sweeper can use this to read the keys in the order of expiry timestamp
 *      2. When you put a key and the cache is full
 *
 *
 * TreeSet Gotcha:
 *  - It uses compare(a, b) == 0 for finding duplicates not equals.
 *  - Hence it will treat two keys with same expiryTime as duplicate and won't add
 * Should you choose a heap or TreeSet?
 *  - In Java, TreeSet .getFirst() is O(logN) operation and not O(1) like cpp
 * Heap Gotcha:
 *
 */


interface EvictionStrategy<K> {
    void onAccess(K key);

    void onAdd(K key);

    void onRemove(K key);

    Optional<K> getEvictionCandidate();
}

class LRUStrategy<K> implements EvictionStrategy<K> {
    private final LinkedHashSet<K> order = new LinkedHashSet<>();

    @Override
    public void onAccess(K key) {
        // Move to the end to mark as most recently used
        order.remove(key);
        order.add(key);
    }

    @Override
    public void onAdd(K key) {
        order.add(key);
    }

    @Override
    public void onRemove(K key) {
        order.remove(key);
    }

    @Override
    public Optional<K> getEvictionCandidate() {
        if (order.isEmpty()) return Optional.empty();
        // The first element is the least recently used
        return Optional.of(order.stream().toList().get(0));
    }
}

//class LFUStrategy<K> implements EvictionStrategy<K> {
//}

// NEW:
//interface Clock {
//    long nowMillis();
//}

// NEW:
//class SystemClock implements Clock {
//}
// NEW:
//class FakeClock implements Clock {
//}

public class CacheWithTtl<K, V> {
    private static final long NEVER = Long.MAX_VALUE;
    private final int capacity;
    // NEW: Instead of storing V, we store Entry<V value, Instant expiresAt>
    private final Map<K, Entry<K, V>> storage;
    private final EvictionStrategy<K> evictionStrategy;
    // NEW: Add defaultTtl and clock
    private final Duration defaultTtl;
    private final Clock clock;


    // NEW:
    // Comparator MUST be total over distinct refs: deadline first, then unique seq.
    // Comparing on expiresAt alone would make TreeSet drop same-deadline keys.
    private final TreeSet<ExpiryRef<K>> expiryIndex = new TreeSet<>(
            Comparator.comparingLong((ExpiryRef<K> r) -> r.expiresAt())
                    .thenComparingLong(ExpiryRef::seq));

    // Position in the expiry index. seq for the TreeSet gotcha.
    private record ExpiryRef<K>(K key, long expiresAt, long seq) {}

    // ref == null means "no TTL".
    private record Entry<K, V>(V value, ExpiryRef<K> ref) {
        boolean isExpired(long now) { return ref != null && now >= ref.expiresAt(); }
    }
    private long seqCounter = 0;

    CacheWithTtl(int capacity, EvictionStrategy<K> evictionStrategy, Duration defaultTtl, Clock clock) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be greater than 0");

        this.capacity = capacity;
        this.storage = new HashMap<>();
        this.evictionStrategy = Objects.requireNonNull(evictionStrategy);
        this.defaultTtl = Objects.requireNonNull(defaultTtl);
        this.clock = Objects.requireNonNull(clock);
    }

    public Optional<V> get(K key) {
        Objects.requireNonNull(key);
        if (!storage.containsKey(key)) {
            return Optional.empty();
        }
        CacheWithTtl.Entry<K, V> entry = storage.get(key);
        // NEW: LAZY deletion
        if (entry.isExpired(clock.now())) {
            // evict the key from Cache and order
            storage.remove(key);
            evictionStrategy.onRemove(key);
            return Optional.empty();
        }
        evictionStrategy.onAccess(key);
        return Optional.of(entry.value);
    }

    public void put(K key, V value) {
        put(key, value, Duration.ofSeconds(0)); // default behavior is never expire
    }

    public void put(K key, V value, Duration ttl) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(ttl);

        long expiresAt = ttl.isZero() ? NEVER : clock.now() + ttl.toMillis();
        ExpiryRef<K> expiryRef = expiresAt == NEVER ? null : new ExpiryRef<>(key, expiresAt, seqCounter++);
        if (expiryRef != null) {
            expiryIndex.add(expiryRef);
        }
        Entry<K, V> entry = new Entry<>(value, expiryRef);
        // Overriding a key -> no eviction happens here
        if (storage.containsKey(key)) {
            storage.put(key, entry);
            evictionStrategy.onAccess(key);
            return;
        }
        // Adding a new key -> eviction happens
        if (storage.size() == capacity) {
            makeRoom();

        }
        // Add the new key
        storage.put(key, entry);
        evictionStrategy.onAdd(key);
    }
    private void makeRoom() {
        // NEW: First delete an expired key if it is available
        if (deleteExpired()) {
            return; // return if room made by expired key
        }

        // Otherwise delete a non-expired entry
        Optional<K> evictionCandidate = evictionStrategy.getEvictionCandidate();
        if (evictionCandidate.isPresent()) {
            Entry<K, V> removed = storage.remove(evictionCandidate.get());
            if (removed.ref != null) expiryIndex.remove(removed.ref);
            evictionStrategy.onRemove(evictionCandidate.get());
            System.out.println("Evicted " + evictionCandidate.get());
        }
    }

    private boolean deleteExpired() {
        long now = clock.now();
        if (!expiryIndex.isEmpty() && expiryIndex.first().expiresAt() <= now) {
            ExpiryRef<K> ref = expiryIndex.pollFirst();
            storage.remove(ref.key());
            evictionStrategy.onRemove(ref.key());
            System.out.println("Expired " + ref.key());
            return true;
        }
        return false; // nothing got removed.
    }




    public static void main(String[] args) {
        Duration neverExpires = Duration.ofDays(1_000);
        FakeClock clock = new FakeClock(1_000); // Start clock at 1 sec from Epoch
        CacheWithTtl<Integer, String> lruCache = new CacheWithTtl<>(2, new LRUStrategy<>(), neverExpires, clock);
        // When no expiry is set: the lru one is removed
        lruCache.put(1, "one");
        lruCache.put(2, "two");
        lruCache.put(3, "three"); // should remove 1

        // test that when the expiry is set the one with the expiry is removed first and not lru
//        lruCache = new CacheWithTtl<>(2, new LRUStrategy<>(), neverExpires, clock);
//        lruCache.put(1, "one");
        lruCache.put(2, "two", Duration.ofSeconds(1));
        clock.advance(Duration.ofSeconds(2));
        lruCache.put(3, "three"); // should remove 2

    }
}