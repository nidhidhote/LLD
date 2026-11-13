import java.util.HashMap;
import java.util.Map;
public class Cache<K,V> {
    private final int capacity;
    private final Map<K,Node<K,V>>cacheMap;
    private final EvictionStrategy<K,V> evictionStrategy;
    Cache(int capacity,EvictionStrategy<K,V> evictionStrategy){
        if (capacity<=0)throw new IllegalArgumentException("Capacity must be greater than 0");
        if (evictionStrategy == null)throw new IllegalArgumentException("Eviction strategy must be define");
        this.capacity = capacity;
        this.evictionStrategy = evictionStrategy;
        this.cacheMap = new HashMap<>();
    }

    public void put(K key,V value){
        if (key == null)throw new IllegalArgumentException("Key is null");
        if (cacheMap.containsKey(key)){
            Node node = cacheMap.get(key);
            node.setValue(value);
            return;
        }
        if (cacheMap.size()==capacity){
            Node evictionCandidate = evictionStrategy.getEvictionCandidate();
            cacheMap.remove(evictionCandidate.getKey());
            evictionStrategy.onRemove(evictionCandidate);
            System.out.println("The eviction candidate is "+evictionCandidate.getKey()+" "+evictionCandidate.getValue()+" "+evictionCandidate.getFrequency());
        }
        Node newNode = new Node(key,value);
        cacheMap.put(key,newNode);
        evictionStrategy.onAdd(newNode);
    }
    public V get(K key){
        if (key == null)throw new IllegalArgumentException("Key is null");
        if(cacheMap.containsKey(key)){
            evictionStrategy.onAccess(cacheMap.get(key));
            return cacheMap.get(key).getValue();
        }
        return null;
    }

    public static void main(String[] args) {
        Cache cache = new Cache(3,new LFU());
        cache.put(1,"A");
        cache.put(2,"B");
        cache.put(3,"C");
//        cache.get(2);
        cache.put(4,"D");
        cache.put(5,"E");
        cache.get(4);
        cache.get(5);
        cache.get(3);
        cache.put(6,"B");
        cache.get(6);
        cache.get(6);
        cache.put(7,"F");
        cache.put(8,"G");
        cache.put(9,"G");
    }
}
