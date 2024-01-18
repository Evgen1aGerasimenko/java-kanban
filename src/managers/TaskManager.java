package managers;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

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
    void updateSubtask(Subtask subtask) throws ManagerSaveException;
    void updateEpic(Epic epic) throws ManagerSaveException;
    void deleteTask(int id) throws ManagerSaveException;
    void deleteSubtask(int id) throws ManagerSaveException;
    void deleteEpic(int id) throws ManagerSaveException;
    void clearAllTasks() throws ManagerSaveException;
    void clearAllSubtasks() throws ManagerSaveException;
    void clearAllEpics() throws ManagerSaveException;
    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}
