package managers;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();
    List<Subtask> getSubtasksOfTasks(int epicId);
    Task getTask(int id) throws ManagerSaveException;
    Subtask getSubtask(int id) throws ManagerSaveException;
    Epic getEpic(int id) throws ManagerSaveException;
    int createTask(Task task) throws ManagerSaveException;
    int createSubtask(Subtask subtask) throws ManagerSaveException;
    int createEpic(Epic epic) throws ManagerSaveException;
    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);
    void deleteTask(int id) throws ManagerSaveException;
    void deleteSubtask(int id) throws ManagerSaveException;
    void deleteEpic(int id) throws ManagerSaveException;
    void clearAllTasks() throws ManagerSaveException;
    void clearAllSubtasks() throws ManagerSaveException;
    void clearAllEpics() throws ManagerSaveException;
    List<Task> getHistory();
}
