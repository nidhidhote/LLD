import java.time.Duration;
import java.util.*;

interface Clock{
    long now();
}

public class Cache<K,V>{

private final int capacity;
private final Map<K,Entry<K,V>>storage;
private final Duration defaultDuration;
private int seqCounter=0;
private static final long NEVER = Long.MAX_VALUE;
private final Clock clock;
private final Strategy<K,V>strategy;
Cache(int capacity,Strategy strategy,Duration defaultDuration,Clock clock){
    if(capacity<1)throw new IllegalArgumentException("Capacity must be greater than 0");
    this.capacity = capacity;
    this.strategy=strategy;
    this.defaultDuration=defaultDuration;
    this.storage = new HashMap<>();
    this.clock=clock;
}
    record Entry<K,V>(V value,ExpireTimeRef<K>expireTimeRef){
    boolean isExpired(long currentTime){
        return expireTimeRef!=null && currentTime>=expireTimeRef.expiresAt;
    }
}
    record ExpireTimeRef<K>(K key,long expiresAt,long seq){}
    private TreeSet<ExpireTimeRef<K>>expireTimeRefTreeSet = new TreeSet<>(
            Comparator.comparingLong((ExpireTimeRef<K>ref)->ref.expiresAt).thenComparingLong(ExpireTimeRef::seq)
    );
public void put(K key,V value){
    put(key,value,Duration.ofSeconds(0));
}
    public void put(K key,V value,Duration ttl){
       long currTime = clock.now();
       long expiryTime = ttl.isZero()?NEVER:currTime+ ttl.toMillis();
       ExpireTimeRef<K>expireTimeRef = (expiryTime==NEVER)?null:new ExpireTimeRef<>(key,expiryTime,seqCounter++);
       Entry<K,V>entry = new Entry<>(value,expireTimeRef);
       if(expireTimeRef!=null)expireTimeRefTreeSet.add(expireTimeRef);
       if(storage.containsKey(key)){
           storage.put(key,entry);
           strategy.onAccess(key);
           return;
       }
       if(storage.size()==capacity){
           makeRoom();
       }
       storage.put(key,entry);
       strategy.onAdd(key);
    }
    private boolean deleteExpire(){
        if(!expireTimeRefTreeSet.isEmpty() && expireTimeRefTreeSet.first().expiresAt<= clock.now()){
            ExpireTimeRef<K>expireTimeRef = expireTimeRefTreeSet.pollFirst();
            storage.remove(expireTimeRef.key);
            strategy.onRemove(expireTimeRef.key);
            System.out.println(expireTimeRef.key+" key got expired");
            return true;
        }
        return false;
    }
    private void makeRoom(){
        if(deleteExpire()){
            return;
        }
        //1.delete an expired key
        //2. delete LRU
     K evictionCandidate = strategy.getEvictionCandidate();
     Entry<K,V>entry = storage.get(evictionCandidate);
     if(entry.expireTimeRef!=null)expireTimeRefTreeSet.remove(entry.expireTimeRef);
     storage.remove(evictionCandidate);
     strategy.onRemove(evictionCandidate);
        System.out.println("Evicted candidate key is "+evictionCandidate);
    }
    public V get(K key){
        Objects.requireNonNull(key);
        if(!storage.containsKey(key))return null;
        long currTime = clock.now();
        if(storage.get(key).isExpired(currTime)){
            storage.remove(key);
            strategy.onRemove(key);
            System.out.println(key+" Key got expired");
            return null;
        }
        strategy.onAccess(key);
        Entry<K,V>entry = storage.get(key);
        return entry.value;
    }

    public static void main(String[] args) {
        Duration neverExpires = Duration.ofDays(1_000);
        FakeClock clock = new FakeClock(1_000);
        Cache cache = new Cache(2,new LRU(),neverExpires,clock);
        cache.put(1,"one");
        cache.put(2,"two");
        cache.put(3,"three");//1 should get remove
        cache.put(4,"four",Duration.ofSeconds(1));
       //
        clock.advance(Duration.ofSeconds(2));
        cache.put(5,"five",Duration.ofSeconds(1));


    }
}
