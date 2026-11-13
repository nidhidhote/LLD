public class LRU implements EvictionStrategy {
    DoublyLinkedList doublyLinkedList = new DoublyLinkedList();
    @Override
    public void onAdd(Node node) {
        if (node == null)throw new IllegalArgumentException("node must be present");
        doublyLinkedList.addNode(node);
    }

    @Override
    public void onAccess(Node node) {
        if (node == null)throw new IllegalArgumentException("node must be present");
        doublyLinkedList.removeNode(node);
        doublyLinkedList.addNode(node);
    }

    @Override
    public void onRemove(Node node) {
        if (node == null)throw new IllegalArgumentException("node must be present");
        doublyLinkedList.removeNode(node);
    }

    @Override
    public Node getEvictionCandidate() {
        Node evictionCandidate = doublyLinkedList.head.next;
        System.out.println("The eviction candidate is "+evictionCandidate.getKey()+" "+evictionCandidate.getValue());
        return evictionCandidate;
    }
}
