import java.util.Objects;

public class Node<K,V> {
     K key;
     V value;
     int frequency;
    Node<K,V>next;
    Node<K,V>prev;
    Node(K key,V value){
//        Objects.requireNonNull(key);
//        Objects.requireNonNull(value);
        this.key = key;
        this.value=value;
        this.frequency = 1;
    }
}
