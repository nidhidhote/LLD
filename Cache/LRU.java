package Cache;

public class LRU<K> implements EvictionStrategy<K>{
    DoublyLinkedList doublyLinkedList = new DoublyLinkedList();
    //private final int capacity;
//    LRU(int capacity){
//        this.capacity = capacity;
//    }
    @Override
    public void onAdd(K key, Node node){
        doublyLinkedList.addNode(node);
    }
    @Override
    public void onRemove(Node node){
        doublyLinkedList.removeNode(node);
    }
    @Override
    public void onAccess(Node node){
       doublyLinkedList.removeNode(node);
       doublyLinkedList.addNode(node);
    }
    @Override
    public Node getEvictionCandidate(){
        System.out.print(doublyLinkedList.head.next.getKey());
        return  doublyLinkedList.head.next;
    }
}
