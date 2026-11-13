package cachePractice1;

import java.util.LinkedList;

interface EvictionStrategy<K,V>{
    void onAdd(Node<K,V>node);
    void onRemove(Node<K,V>node);
    void onAccess(Node<K,V>node);
    K getEvictionCandidate();

}
public class LRU<K,V> implements EvictionStrategy<K,V> {
    private final linkedList<K,V>linkedList = new linkedList<>();
    @Override
    public void onAdd(Node<K,V>node){
//        if (node == null)throw new IllegalArgumentException("node is null");
        linkedList.add(node);
    }
    @Override
    public void onRemove(Node<K,V>node){
//        if (node == null)throw new IllegalArgumentException("node is null");
        linkedList.remove(node);
    }
    @Override
    public void onAccess(Node<K,V>node){
        linkedList.remove(node);
        linkedList.add(node);
    }
    @Override
    public K getEvictionCandidate() {
        System.out.println("The eliminated node is "+ linkedList.getFirst().getKey()+"  "+linkedList.getFirst().getValue());
        return (K)linkedList.getFirst().getKey();
    }
}
