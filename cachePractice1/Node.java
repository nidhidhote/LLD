package cachePractice1;

public class Node <K,V>{
    private K key;
    private V value;
    public Node<K,V> prev;
    public Node<K,V> next;
    Node(K key, V value){
        this.key = key;
        this.value = value;
    }
    V getValue(){
        return this.value;
    }
    K getKey(){
        return this.key;
    }
}
