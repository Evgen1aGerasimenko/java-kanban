package managers;
import tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        public Task data;
        public Node next;
        public Node prev;


        public Node(Task data, Node next, Node prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    protected final Map<Integer, Node> history = new HashMap<>();
    protected Node first;
    protected Node last;

    @Override
    public void add(Task task) {
        Node node = history.get(task.getId());
            removeNode(node);
            linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.clear();
        Node node = first;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }

    private void linkLast(Task task) {
        final Node tail = last;
        final Node newNode = new Node(task, null, tail);
        last = newNode;
        if (tail == null) {
            first = newNode;
        } else {
            tail.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }

    private void removeNode(Node node) {
            if (node == null) {
                return;
            }
            if (first.equals(node) && last.equals(node)){
                first = null;
                last = null;
            }
            else if (first.equals(node)) {
                first = node.next;
                first.prev = null;
            } else if (last.equals(node)) {
                last = node.prev;
                last.next = null;
            }else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            history.remove(node.data.getId());
    }
}
