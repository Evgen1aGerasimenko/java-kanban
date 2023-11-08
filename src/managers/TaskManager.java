package managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();
    List<Subtask> getSubtasksOfTasks(int epicId);
    Task getTask(int id);
    Subtask getSubtask(int id);
    Epic getEpic(int id);
    int createTask(Task task);
    int createSubtask(Subtask subtask);
    int createEpic(Epic epic);
    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);
    void deleteTask(int id);
    void deleteSubtask(int id);
    void deleteEpic(int id);
    void clearAllTasks();
    void clearAllSubtasks();
    void clearAllEpics();
    List<Task> getHistory();
}
