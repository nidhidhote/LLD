import java.util.Map;
import java.util.HashMap;
public class LFU<K,V>implements Strategy<K,V>{
    private final Map<Integer,DDL<K,V>>freqMap;
    DDL<K,V> doublyLinkedList;
    private int minFreq;
    LFU(){
        freqMap = new HashMap<>();
        minFreq = 0;
    }
    @Override
    public void onAdd(Node<K,V> node) {
        int freq = node.frequency;
        if(freqMap.containsKey(freq)){
            doublyLinkedList = freqMap.get(freq);
        }else{
            minFreq=1;
            doublyLinkedList = new DDL<>();
        }
        doublyLinkedList.addNode(node);
        freqMap.put(freq,doublyLinkedList);
    }

    @Override
    public void onRemove(Node<K,V> node) {
        int freq = node.frequency;
        if (freqMap.containsKey(freq)){
            doublyLinkedList = freqMap.get(freq);
            doublyLinkedList.removeNode(node);
            if(doublyLinkedList.isEmpty()){
                freqMap.remove(freq);
                if (freq == minFreq)minFreq++;
            }
        }
    }
    @Override
    public void onAccess(Node<K,V> node) {
     onRemove(node);
     int freq = node.frequency;
     node.frequency+=1;
     if (freqMap.containsKey(freq+1)){
         doublyLinkedList = freqMap.get(freq+1);
         doublyLinkedList.addNode(node);
     }else{
      doublyLinkedList = new DDL<>();
      doublyLinkedList.addNode(node);
      freqMap.put(freq+1,doublyLinkedList);
     }
    }

    @Override
    public Node<K,V> getEvictionCandidate() {
        if (!freqMap.get(minFreq).isEmpty())
        return freqMap.get(minFreq).head.next;
        return null;
    }
}
