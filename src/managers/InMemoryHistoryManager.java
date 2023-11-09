package managers;
import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected List<Task> history = new LinkedList<>();
    @Override
    public void add(Task task) {
        if(history.size() > 9) {
            ((LinkedList<Task>)history).removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
