public class DDL<K,V> {
    Node<K,V>head;
     Node<K,V>tail;
    DDL(){
        head = new Node(null,null);
        tail = new Node(null,null);
        head.next = tail;
        tail.prev=head;
    }
    public void addNode(Node<K,V>node){
        if (node == null)throw new IllegalArgumentException("Node must be present");
        node.next = tail;
        node.prev=tail.prev;
        node.prev.next=node;
        tail.prev=node;
    }
    public void removeNode(Node<K,V>node){
        if (node == null)throw new IllegalArgumentException("Node must be present");
        node.next.prev = node.prev;
        node.prev.next= node.next;
        node.next=null;
        node.prev=null;
    }
    public boolean isEmpty(){
        return head.next==tail;
    }
}
