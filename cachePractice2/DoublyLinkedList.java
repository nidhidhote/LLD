public class DoublyLinkedList<K,V> {
public Node head = new Node(-1,-1);
public Node tail = new Node(-1,-1);
DoublyLinkedList(){
    if (this.head == null || this.tail == null)throw new RuntimeException("Wrong initialization of doubly linked list");
    this.head.next = tail;
    this.tail.prev = head;
}
public void addNode(Node<K,V>node){
    if (node == null)throw new IllegalArgumentException("node can not be null");
    node.next = tail;
    node.prev = tail.prev;
    tail.prev.next=node;
    tail.prev=node;
}

public void removeNode(Node<K,V>node){
    if (node == null) throw new IllegalArgumentException("node can not be null");
        node.prev.next = node.next;
        node.next.prev = node.prev;

//    node.next=null;
//    node.prev=null;
}
public void iterate(){
    Node curr = head;
    while(curr!=tail){
        System.out.print("["+curr.getKey()+" "+curr.getValue()+"] ");
        curr=curr.next;
    }
}

    public static void main(String[] args) {
//        DoublyLinkedList doublyLinkedList = new DoublyLinkedList();
//        Node node =new Node(2,"orange");
//        doublyLinkedList.addNode(new Node(1,"apple"));
//        doublyLinkedList.addNode(node);
//        doublyLinkedList.addNode(new Node(3,"kiwi"));
//        doublyLinkedList.removeNode(node);
//        doublyLinkedList.iterate();
    }

}
