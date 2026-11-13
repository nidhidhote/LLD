package cachePractice1;

public class linkedList<K,V> {
    private Node<K,V> head;
    private Node<K,V> tail;
    linkedList(){
        this.head = new Node(-1,"dummyHead");
        this.tail = new Node(-1,"dummyTail");
        head.next = tail;
        tail.prev = head;
    }
    public void add(Node node){
        if (node == null)throw new IllegalArgumentException("Node is null");
        node.next = tail;
        node.prev = tail.prev;
        tail.prev.next = node;
        tail.prev = node;
    }
    public void remove(Node node){
        if (node == null)throw new IllegalArgumentException("Node is null");
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.next=null;
        node.prev=null;
    }
    public Node<K,V>getFirst(){
        return this.head.next;
    }
}
