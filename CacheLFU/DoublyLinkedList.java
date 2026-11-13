public class DoublyLinkedList<K,V>{
    private final Node<K,V>head;
    private final Node<K,V>tail;
    DoublyLinkedList(){
        this.head = new Node<>();
        this.tail = new Node<>();
        head.next=tail;
        tail.prev=head;
    }
    public boolean isEmpty(){
        return head.next == tail;
    }
    public void addLast(Node<K,V>node){
        if (node == null)throw new IllegalArgumentException("Node is null");
        node.next=tail;
        node.prev=tail.prev;
        node.prev.next = node;
        tail.prev=node;
    }
    public void remove(Node<K,V>node){
        if (node == null || node.next == null || node.prev==null)throw new IllegalArgumentException("Node is null");
        node.next.prev=node.prev;
        node.prev.next=node.next;
        node.next=null;
        node.prev=null;
    }
    public Node<K,V>getFirst(){
        return head.next;
    }
}
