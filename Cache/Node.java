package Cache;

public class Node<K>{
   private K key;
   public Node prev;
   public Node next;
    Node(K key){
        this.key = key;
//        this.next = null;
//        this.prev = null;
   }
   public K getKey(){
        return this.key;
   }
}
