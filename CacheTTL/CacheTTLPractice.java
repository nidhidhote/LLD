import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheTTLPractice<K,V>{
    private int capacity;
    private final Strategy<K,V>strategy;
    private final long NEVER=Long.MAX_VALUE;
    private final Map<K,Entry<K,V>> storage;
    private long seqCounter = 0;
    private Clock clock;
//    private final Duration defaultTTL;
    record Entry<K,V>(V value, ExpireTimeRef<K>expireTimeRef){
        boolean isExpired(long currTime){
            return expireTimeRef!=null && currTime>=expireTimeRef.expireAt;
        }
    }
    record ExpireTimeRef<K>(K key,long expireAt,long seq){}
    CacheTTLPractice(int capacity,Strategy<K,V>strategy,Clock clock){
        if(capacity<1)throw new IllegalArgumentException("Capacity must be greater than 0");
        Objects.requireNonNull(strategy);
        Objects.requireNonNull(clock);
        this.capacity = capacity;
        this.strategy = strategy;
        this.clock = clock;
        storage = new HashMap<>();
//        this.defaultTTL = defaultTTL;
    }
    private TreeSet<ExpireTimeRef<K>>expiryIndex = new TreeSet<>(
            Comparator.comparingLong((ExpireTimeRef<K>ref)->ref.expireAt).thenComparingLong(ExpireTimeRef::seq)
    );

    public void put(K key,V value){
        put(key,value, Duration.ofSeconds(0));
    }
    public void put(K key,V value,Duration ttl){
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(ttl);
    long expiryTime = ttl.isZero()?NEVER: clock.now()+ttl.toMillis();
    if(storage.containsKey(key)){
        Entry<K,V>entry = storage.get(key);
        if(entry!=null && entry.expireTimeRef!=null){
            expiryIndex.remove(entry.expireTimeRef);
        }
        ExpireTimeRef<K>expireTimeRef = (expiryTime==NEVER)?null:new ExpireTimeRef<>(key,expiryTime,seqCounter++);
        entry = new Entry<>(value,expireTimeRef);
        if(expireTimeRef!=null)expiryIndex.add(expireTimeRef);
        storage.put(key,entry);
        strategy.onAccess(key);
        return;
    }
    if(storage.size()==capacity){
        makeRoom();
        //delete expired one
        //delete LRU
    }
    ExpireTimeRef<K>newExpireTimeRef = (expiryTime==NEVER)?null:new ExpireTimeRef<>(key,expiryTime,seqCounter++);
    Entry<K,V>newEntry = new Entry<>(value,newExpireTimeRef);
    if(newExpireTimeRef!=null)expiryIndex.add(newExpireTimeRef);
    storage.put(key,newEntry);
    strategy.onAdd(key);
    }
    private ScheduledExecutorService scheduledExecutorService;


    public void startSweep(Duration initialDelay, int budget){
        if(scheduledExecutorService!=null)return;

        Runnable task = new Runnable() {
            @Override
            public void run() {
                deleteExpires(budget);
            }
        };
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor((runnable)->{
            Thread thread = new Thread(runnable,"background-sweeper");
            thread.setDaemon(true);
            return thread;
        });
        scheduledExecutorService.scheduleWithFixedDelay(task,initialDelay.toMillis(),2, TimeUnit.MILLISECONDS);
    }
    private int deleteExpires(int budget){
        int budgetCounter=0;
        long currTime = clock.now();
        while(!expiryIndex.isEmpty()&&expiryIndex.first().expireAt<=currTime && budgetCounter<budget){
            ExpireTimeRef<K>expireTimeRef = expiryIndex.pollFirst();
            storage.remove(expireTimeRef.key);
            strategy.onRemove(expireTimeRef.key);
            System.out.println("Expired key is "+expireTimeRef.key+" delete counter "+budgetCounter);
            budgetCounter++;
//            return true;
        }
        return budgetCounter;
    }

    private void makeRoom(){
        int budget = 10;
        if(deleteExpires(budget)>0){
            return;
        }
        K evicted = strategy.getEvictionCandidate();
        Entry<K,V>entry = storage.remove(evicted);
        strategy.onRemove(evicted);
        System.out.println("Evicted key is "+evicted);
        if(entry.expireTimeRef!=null)expiryIndex.remove(entry.expireTimeRef);
        return;
    }
    public Optional<V>get(K key){
        Objects.requireNonNull(key);
        if(storage.containsKey(key))return Optional.empty();
        Entry<K,V>entry = storage.get(key);
        if(entry.isExpired(clock.now())){
            storage.remove(key);
            strategy.onRemove(key);
            if(entry.expireTimeRef!=null)expiryIndex.remove(entry.expireTimeRef);
            System.out.println("Expired key is "+key);
        }
        strategy.onAccess(key);
        return Optional.of(storage.get(key).value);
    }

    public static void main(String[] args) {
//        Duration defaultTTL = Duration.ofDays(1_000);
        FakeClock clock = new FakeClock(1_000);
        CacheTTLPractice cacheTTLPractice = new CacheTTLPractice(1,new LRU<>(),clock);
        cacheTTLPractice.put(1,"one");
        cacheTTLPractice.put(2,"two",Duration.ofSeconds(1));
        clock.advance(Duration.ofSeconds(2));
        cacheTTLPractice.put(3,"three",Duration.ofSeconds(2));
        cacheTTLPractice.put(4,"four",Duration.ofSeconds(1));
        cacheTTLPractice.startSweep(Duration.ofSeconds(2),4);
    }
}
