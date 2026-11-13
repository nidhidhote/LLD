package Cache;

public class DoublyLinkedList{
    Node head = new Node(-1);
    Node tail = new Node(-1);
  //  private int length;
    DoublyLinkedList(){
     //   this.length = 0;
        if (head != null && tail!=null){
            head.next = tail;
            tail.prev = head;
        }
    }
    public void addNode(Node node){
        if (node == null)return;
        if(head == null || tail == null)return;
      //  this.length+=1;
        node.next = tail;
        node.prev = tail.prev;
        tail.prev.next = node;
        tail.prev = node;
    }
    public void removeNode(Node node){
        if (node == null || head == null || tail == null)return;
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.next=null;
        node.prev=null;
    }
//    public int getLength(){
//        return this.length;
//    }
    public void printList(){
        Node currentNode = tail.next;
        while (currentNode!=tail){
            System.out.print(currentNode.getKey()+"<->");
            currentNode=currentNode.next;
        }
        System.out.print("null");
    }
    public static void main(String[] args) {
        DoublyLinkedList doublyLinkedList = new DoublyLinkedList();
        Node node = new Node(1);
        doublyLinkedList.addNode(node);
        doublyLinkedList.addNode(new Node(2));
        doublyLinkedList.addNode(new Node(3));
        doublyLinkedList.addNode(new Node(4));
        doublyLinkedList.addNode(new Node(5));
        doublyLinkedList.addNode(new Node(6));

        doublyLinkedList.removeNode(node);
        doublyLinkedList.printList();
        System.out.println();
        doublyLinkedList.addNode(node);
        doublyLinkedList.printList();

    }
}
