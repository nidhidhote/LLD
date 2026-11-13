import java.util.Map;
import java.util.HashMap;
public class LFU<K,V> implements EvictionStrategy<K,V>{

    private final Map<Integer,DoublyLinkedList<K,V>>frequencyMap;

    private int minFrequency;
//    private int currMinFreq;
    private DoublyLinkedList<K,V> doublyLinkedList;
    LFU(){
        frequencyMap = new HashMap<>();
        minFrequency = 1;
//        currMinFreq=1;
    }
    @Override
    public void onAdd(Node<K,V>node) {
        node.setFrequency(1);
        minFrequency=1;
        if(frequencyMap.containsKey(1)){
            frequencyMap.get(1).addLast(node);
        }else{
            doublyLinkedList = new DoublyLinkedList<>();
            doublyLinkedList.addLast(node);
            frequencyMap.put(1,doublyLinkedList);
        }
    }

    @Override
    public void onRemove(Node<K,V>node) {
      int freq = node.getFrequency();
      if(frequencyMap.containsKey(freq)){
          frequencyMap.get(freq).remove(node);
      }
      minFrequency = getMinFrequency(freq);
    }
    public int getMinFrequency(int freq){
        if (frequencyMap.containsKey(freq)&& frequencyMap.get(freq).isEmpty()){
            frequencyMap.remove(freq);
            if(freq == minFrequency)
            return minFrequency+1;
        }
        return minFrequency;
    }

    @Override
    public void onAccess(Node<K,V>node) {
     int freq = node.getFrequency();
     node.setFrequency(freq+1);
     if(frequencyMap.containsKey(freq)){
         frequencyMap.get(freq).remove(node);
         minFrequency = getMinFrequency(freq);
     }
     if(frequencyMap.containsKey(freq+1)){
         frequencyMap.get(freq+1).addLast(node);
     }else{
         doublyLinkedList = new DoublyLinkedList();
         doublyLinkedList.addLast(node);
         frequencyMap.put(freq+1,doublyLinkedList);
     }
    }

    @Override
    public Node<K,V> getEvictionCandidate() {
        if(frequencyMap.containsKey(minFrequency)) {
            return frequencyMap.get(minFrequency).getFirst();
        }
        return null;
    }
}
