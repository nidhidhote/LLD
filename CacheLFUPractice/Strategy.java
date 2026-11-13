public interface Strategy<K,V>{
    void onAdd(Node<K,V>node);
    void onRemove(Node<K,V>node);
    void onAccess(Node<K,V>node);
    Node<K,V>getEvictionCandidate();
}
