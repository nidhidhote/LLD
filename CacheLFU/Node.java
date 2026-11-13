public class Node<K,V>{
    private K key;
    private V value;

    private int frequency;

    public Node<K,V>prev;
    public Node<K,V>next;

    Node(){

    }

    Node (K key,V value){
        this.key=key;
        this.value=value;
        this.prev=null;
        this.next=null;
    }
    public void setValue(V value){
        this.value = value;
    }
    public V getValue(){
        return this.value;
    }
    public K getKey(){
        return this.key;
    }
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }
    public int getFrequency(){
        return this.frequency;
    }
}
