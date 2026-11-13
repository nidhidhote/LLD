package cachePractice1;
import java.util.Map;
import java.util.HashMap;

public class Cache<K,V> {
    private final Map<K,Node<K,V>>cacheMap = new HashMap<>();
    private final int capacity;
    private final EvictionStrategy evictionStrategy;
    Cache(int c, EvictionStrategy<K,V>evictionStrategy){
        this.capacity = c;
        this.evictionStrategy = evictionStrategy;
    }
    public void get(K key){
        if (cacheMap.containsKey(key))
            evictionStrategy.onAccess(cacheMap.get(key));
        else throw new IllegalArgumentException("Key is not present");
        return;
    }
    public void put(K key, V value){
        Node node = new Node(key, value);
        if (cacheMap.containsKey(key)){
            cacheMap.put(key,node);
            evictionStrategy.onAccess(cacheMap.get(key));
            return;
        }
        if (cacheMap.size()==capacity){
            K evictionCandidate = (K)evictionStrategy.getEvictionCandidate();
            cacheMap.remove(evictionCandidate);
        }
        cacheMap.put(key,node);
        evictionStrategy.onAdd(node);
    }

    public static void main(String[] args) {
        Cache cache = new Cache(3, new LRU<>());
        //absent key
//        cache.get(1);
        cache.put(1,"Apple");
        cache.put(2,"Orange");
        cache.get(1);
        cache.put(3,"Mango");
        cache.put(4,"Kiwi");


    }
}
