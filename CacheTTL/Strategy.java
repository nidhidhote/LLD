public interface Strategy<K,V> {
    void onAdd(K key);
    void onAccess(K key);
    void onRemove(K key);
    K getEvictionCandidate();
}
