public class Node<K,V> {
    private K key;
    private V value;

    public Node<K,V>prev;

    public Node<K,V>next;

    Node(K key,V value){
        if (key== null || value == null)throw new IllegalArgumentException("Key and value must be present for a node");
        this.key = key;
        this.value = value;

    }
    public K getKey(){
        return this.key;
    }
    public V getValue(){
        return this.value;
    }
    public void setValue(V value){
        this.value = value;
    }

}
