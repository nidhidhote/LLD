import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CacheLHM<K,V>{
    private final Map<K,V> cacheMap;
    private final int capacity;
    private final EvictionStrategy<K,V> evictionStrategy;
    CacheLHM(int capacity,EvictionStrategy evictionStrategy){
        if (capacity<=0)throw new IllegalArgumentException("Capacity must be greater than 0");
        if (evictionStrategy == null) throw new IllegalArgumentException("Evictionstrategy can't be null");
        this.capacity = capacity;
        this.evictionStrategy = evictionStrategy;
        this.cacheMap = new HashMap<>();
    }
    public V get(K key){
        if (!cacheMap.containsKey(key))
        return null;
        evictionStrategy.onAccess(key);
        return cacheMap.get(key);
    }
    public void put(K key,V value){
        if (cacheMap.containsKey(key)){
            cacheMap.put(key,value);
            evictionStrategy.onAccess(key);
            return;
        }
        if (cacheMap.size()==capacity){
            K evictionCandidate = evictionStrategy.getEvictionCandidate();
            cacheMap.remove(evictionCandidate);
            evictionStrategy.onRemove(evictionCandidate);
        }
        cacheMap.put(key,value);
        evictionStrategy.onAdd(key,value);
    }

    public static void main(String[] args) {
        CacheLHM cacheLHM = new CacheLHM<>(3,new LRU());
        cacheLHM.put(1,"Apple");
        cacheLHM.put(2,"Bat");
        cacheLHM.put(3,"Cat");
        cacheLHM.put(4,"Dog");
        cacheLHM.put(5,"Egg");
        cacheLHM.get(3);
        cacheLHM.put(6,"Fish");

    }

}
