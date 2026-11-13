import java.util.HashMap;
import java.util.Map;
public class Cache<K,V>{

     private final int capacity;
     private final EvictionStrategy evictionStrategy;
     private final Map<K,Node<K,V>>cacheMap = new HashMap<>();
     Cache(int capacity,EvictionStrategy evictionStrategy){
         if (capacity<=0)throw new IllegalArgumentException("Capacity can't be 0 or negative");
         if (evictionStrategy == null)throw new IllegalArgumentException("Need eviction strategy for performing cache operations");
         this.capacity = capacity;
         this.evictionStrategy = evictionStrategy;
     }
    public V get(K key){
       if (cacheMap.isEmpty())return null;
       if (cacheMap.containsKey(key)) {
           evictionStrategy.onAccess(cacheMap.get(key));
           return cacheMap.get(key).getValue();
       }else throw new IllegalArgumentException("Key is not present in the cache");
    }
    public void put(K key,V value){
        if (key == null)throw new IllegalArgumentException("Key can not be empty");
      //  Node node = new Node(key,value);
        if (cacheMap.containsKey(key)){
            Node node = cacheMap.get(key);
            node.setValue(value);
            cacheMap.put(key,node);
            evictionStrategy.onAccess(cacheMap.get(key));
            return;
        }
        if (cacheMap.size()==capacity){
            Node<K,V>evictionCandidate = evictionStrategy.getEvictionCandidate();
            cacheMap.remove(evictionCandidate.getKey());
            evictionStrategy.onRemove(evictionCandidate);
        }
        Node node = new Node(key,value);
        cacheMap.put(key,node);
        evictionStrategy.onAdd(node);
        return;
    }

    public static void main(String[] args) {
        Cache cache = new Cache(3, new LRU());
        cache.put(1,"Apple");
        cache.put(2,"Orange");
        cache.put(3,"Kiwi");
        cache.put(4,"Mango");
        cache.get(2);
        cache.put(5,"Banana");
        cache.put(5,"Papaya");
        System.out.println("Updated value of key 5 is "+ cache.get(5));
    }
}
