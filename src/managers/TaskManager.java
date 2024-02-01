package managers;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    List<Task> getTasks() throws IOException;
    List<Subtask> getSubtasks() throws IOException ;
    List<Epic> getEpics() throws IOException ;
    List<Subtask> getSubtasksOfTasks(int epicId) throws IOException ;
    Task getTask(int id) throws IOException, ManagerSaveException;
    Subtask getSubtask(int id) throws IOException, ManagerSaveException;
    Epic getEpic(int id) throws IOException, ManagerSaveException;
    int createTask(Task task) throws IOException, ManagerSaveException;
    int createSubtask(Subtask subtask) throws IOException, ManagerSaveException;
    int createEpic(Epic epic) throws IOException, ManagerSaveException;
    void updateTask(Task task) throws IOException, ManagerSaveException;
    void updateSubtask(Subtask subtask) throws IOException, ManagerSaveException;
    void updateEpic(Epic epic) throws IOException, ManagerSaveException;
    void deleteTask(int id) throws IOException, ManagerSaveException;
    void deleteSubtask(int id) throws IOException, ManagerSaveException;
    void deleteEpic(int id) throws IOException, ManagerSaveException;
    void clearAllTasks() throws IOException, ManagerSaveException;
    void clearAllSubtasks() throws IOException, ManagerSaveException;
    void clearAllEpics() throws IOException, ManagerSaveException;
    List<Task> getHistory() throws IOException ;
    List<Task> getPrioritizedTasks() throws IOException ;
}
