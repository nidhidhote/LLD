import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LRU<K,V> implements EvictionStrategy<K,V> {
     LinkedHashMap<K,V> linkedHashMap = new LinkedHashMap<>();
    @Override
    public void onAdd(K key,V value) {
//        if (!linkedHashMap.containsKey(key))return;
        linkedHashMap.put(key,value);
    }

    @Override
    public void onRemove(K key) {
      linkedHashMap.remove(key);
    }

    @Override
    public void onAccess(K key) {
      V value = linkedHashMap.get(key);
      linkedHashMap.remove(key);
      linkedHashMap.put(key,value);
    }

    @Override
    public K getEvictionCandidate() {
        Map.Entry<K,V>entry = linkedHashMap.entrySet().iterator().next();
        System.out.println("The evicted candidate is "+entry.getKey() +" "+entry.getValue());
        return entry.getKey();
    }
}
