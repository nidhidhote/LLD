import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class LRU<K,V> implements Strategy<K,V> {
    private final LinkedHashSet<K>lruMap;

    LRU(){
        lruMap = new LinkedHashSet<>();
    }

    @Override
    public void onAdd(K key) {
        lruMap.add(key);
    }

    @Override
    public void onAccess(K key) {
      lruMap.remove(key);
      lruMap.add(key);
    }

    @Override
    public void onRemove(K key) {
     lruMap.remove(key);
    }

    @Override
    public K getEvictionCandidate() {
      K k=lruMap.stream().toList().get(0);
        return k;
    }
}
