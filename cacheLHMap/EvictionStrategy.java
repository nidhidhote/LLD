public interface EvictionStrategy<K,V>{
    void onAdd(K key,V value);
    void onRemove(K key);
    void onAccess(K key);
    K getEvictionCandidate();
}
