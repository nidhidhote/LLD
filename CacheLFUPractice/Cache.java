import java.util.HashMap;
import java.util.Objects;

public class Cache<K,V>{
    private final int capacity;
    private final HashMap<K,Node<K,V>>storage;
    private final Strategy<K,V>strategy;
    Cache(int capacity,Strategy<K,V>strategy){
        if(capacity<1)throw new IllegalArgumentException("Capacity must be greater than 0");
        Objects.requireNonNull(strategy);
        this.capacity = capacity;
        this.storage = new HashMap<>();
        this.strategy = strategy;
    }
    public void put(K key,V value){
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Node<K,V>node;
        if (storage.containsKey(key)){
            node = storage.get(key);
            node.value = value;
//            node.frequency = node.frequency+1;
            strategy.onAccess(node);
            return;
        }
        if (storage.size()==capacity){
            node = strategy.getEvictionCandidate();
            storage.remove(node.key);
            strategy.onRemove(node);
            System.out.println("The evicted candidate is key: "+node.key+" value: "+node.value+" freq: "+node.frequency);
        }
        node = new Node<>(key,value);
        storage.put(key,node);
        strategy.onAdd(node);
    }

    public V get(K key){
        Objects.requireNonNull(key);
        if (storage.containsKey(key)){
            strategy.onAccess(storage.get(key));
            return storage.get(key).value;
        }
        return null;
    }

    public static void main(String[] args) {
        Cache cache = new Cache<>(3,new LFU<>());
        cache.put(1,"A");
        cache.put(2,"B");
        cache.put(3,"C");
        cache.get(1);
        cache.get(2);
        cache.get(3);
        cache.put(2,"D");
        cache.put(4,"E");
        cache.put(5,"F");
        cache.put(6,"G");
        cache.put(7,"H");
        cache.get(6);
        cache.put(2,"I");
        cache.put(3,"K")

        ;
    }
}
