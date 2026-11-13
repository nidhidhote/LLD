package Cache;

import java.util.HashMap;
import java.util.Map;

interface EvictionStrategy<K>{
    void onAdd(K key, Node node);
    void onRemove(Node node);
    void onAccess(Node node);
    Node getEvictionCandidate();
}

public class Cache<K,V> {
    private final Map<K,Node>cacheMap = new HashMap<>();
    private int capacity;
    Cache(int capacity){
        this.capacity = capacity;
    }
    LRU cache = new LRU();
    public Node get(K key){
        Node node =cacheMap.get(key);
        cache.onAccess(cacheMap.get(key));
        return node;
    }
    public void put(K key){
        if (cacheMap.containsKey(key)){
            cacheMap.put(key,cacheMap.get(key));
            cache.onAccess(cacheMap.get(key));
            return;
        }
        Node node = new Node(key);
        if (cacheMap.size()==this.capacity) {
            Node node1 = cache.getEvictionCandidate();
            cacheMap.remove(node1.getKey());
            cache.onRemove(node1);
        }
        cacheMap.put(key, node);
        cache.onAdd(key, node);
    }

    public static void main(String[] args) {
        Cache cache1 = new Cache(3);
        cache1.put(1);
        cache1.put(2);
        cache1.put(3);
        cache1.get(1);
        cache1.put(4);
        cache1.get(4);
        System.out.println();
        cache1.put(5);
//        cache1.
    }
}
