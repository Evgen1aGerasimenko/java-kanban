package managers;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager;
    private int idGenerator = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }
    @Override
    public List<Subtask> getSubtasksOfTasks(int epicId) {
        List<Subtask> newSub = new ArrayList<>();
        List<Subtask> sub = getSubtasks();
        for (Subtask subtask : sub) {
            if (subtask.getEpicId() == epicId) {
                newSub.add(subtask);
            }
        }
        return newSub;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if(task != null) {
            historyManager.add(task);
        }
        return task;
    }
    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if(subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }
    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if(epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }
    @Override
    public int createTask(Task task) {
        int id = ++idGenerator;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }
    @Override
    public int createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            int id = ++idGenerator;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.getSubtaskId().add(id);
            calculateStatus(epic.getId());
            return id;
        } else {
            System.out.println("no such epic to create subtask" + subtask.getEpicId());
            return -1;
        }
    }
    @Override
    public int createEpic(Epic epic) {
        int id = ++idGenerator;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }
    @Override
    public void updateTask(Task task) {
        //tasks.put(task.getId(), task);
        Task taskUpdate = tasks.get(task.getId());
        taskUpdate.setName(task.getName());
        taskUpdate.setStatus(task.getStatus());
        tasks.put(task.getId(), taskUpdate);
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Subtask subtaskUpdate = subtasks.get(subtask.getId());
            subtaskUpdate.setName(subtask.getName());
            Epic epic = epics.get(subtask.getEpicId());
            calculateStatus(epic.getId());
        } else {
            return;
        }
    }
    @Override
    public void updateEpic(Epic epic) {
        Epic epicUpdate = epics.get(epic.getId());
        epicUpdate.setName(epic.getName());
        epics.put(epic.getId(),epicUpdate);
    }
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }
    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        List<Integer> checkList = epic.getSubtaskId();
        for(Integer epicFor : checkList) {
            if (epicFor.equals(id)) {
                epic.getSubtaskId().remove(epicFor);
                break;
            }
        }
        calculateStatus(epic.getId());
        subtasks.remove(id);
        historyManager.remove(id);
    }
    @Override
    public void deleteEpic(int id) {

        Epic epic = epics.get(id);
        List<Integer> idList = epic.getSubtaskId();

        for (Integer idSub : idList) {
            subtasks.remove(idSub);
        }
        epics.remove(id);
        historyManager.remove(id);
    }
    @Override
    public void clearAllTasks(){
        for(Integer task : tasks.keySet()){
            historyManager.remove(task);
        }
        tasks.clear();

    }
    @Override
    public  void clearAllSubtasks(){
        for (Integer subs : subtasks.keySet()) {
            historyManager.remove(subs);
            Subtask subtask = subtasks.get(subs);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskId().clear();
            calculateStatus(epic.getId());
        }
        subtasks.clear();
    }
    @Override
    public void clearAllEpics(){
        for(Integer subs : subtasks.keySet()){
            historyManager.remove(subs);
        }

        for(Integer epic : epics.keySet()){
            historyManager.remove(epic);
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Status calculateStatus(int epicId) {

        Epic epic = epics.get(epicId);
        if (epic == null) return null;
        List<Integer> idList = epic.getSubtaskId();

        if (idList.isEmpty()) {
            return Status.NEW;
        }
        int newStatus = 0;
        int doneStatus = 0;
        int inProgressStatus = 0;
        for (Integer id : idList) {
            if (subtasks.get(id).getStatus().equals(Status.NEW)) newStatus++;
            if (subtasks.get(id).getStatus().equals(Status.DONE)) doneStatus++;
            if (subtasks.get(id).getStatus().equals(Status.IN_PROGRESS)) inProgressStatus++;
        }
        if (newStatus > 0 && doneStatus == 0 && inProgressStatus == 0) {
            epic.setStatus(Status.NEW);
        } else if (newStatus == 0 && doneStatus > 0 && inProgressStatus == 0) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        return epic.getStatus();
    }

}

