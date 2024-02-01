package managers;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected static HistoryManager historyManager;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,  Comparator.nullsLast(Comparator.naturalOrder())).thenComparingInt(Task::getId));
    private int idGenerator = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getTasks() throws IOException {
        return new ArrayList<>(tasks.values());
    }
    @Override
    public List<Subtask> getSubtasks() throws IOException {
        return new ArrayList<>(subtasks.values());
    }
    @Override
    public List<Epic> getEpics() throws IOException {
        return new ArrayList<>(epics.values());
    }
    @Override
    public List<Subtask> getSubtasksOfTasks(int epicId) throws IOException {
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
    public Task getTask(int id) throws IOException, ManagerSaveException {
        Task task = tasks.get(id);
        if(task != null) {
            historyManager.add(task);
        }
        return task;
    }
    @Override
    public Subtask getSubtask(int id) throws IOException, ManagerSaveException {
        Subtask subtask = subtasks.get(id);
        if(subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }
    @Override
    public Epic getEpic(int id) throws IOException, ManagerSaveException {
        Epic epic = epics.get(id);
        if(epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }
    @Override
    public int createTask(Task task) throws IOException, ManagerSaveException {
        add(task);
        int id = ++idGenerator;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }
    @Override
    public int createSubtask(Subtask subtask) throws IOException, ManagerSaveException {
        add(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            int id = ++idGenerator;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.getSubtaskId().add(id);
            updateEpic(epic);

            return id;
        } else {
            System.out.println("no such epic to create subtask" + subtask.getEpicId());
            return -1;
        }
    }
    @Override
    public int createEpic(Epic epic) throws IOException, ManagerSaveException {
        int id = ++idGenerator;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }
    @Override
    public void updateTask(Task task) throws IOException, ManagerSaveException {
        Task taskUpdate = tasks.get(task.getId());
        prioritizedTasks.remove(task);
        taskUpdate.setName(task.getName());
        taskUpdate.setStatus(task.getStatus());
        taskUpdate.setDescription(task.getDescription());
        taskUpdate.setDuration(task.getDuration());
        taskUpdate.setStartTime(task.getStartTime());
        tasks.put(task.getId(), taskUpdate);
        add(task);
    }
    @Override
    public void updateSubtask(Subtask subtask) throws IOException, ManagerSaveException {
        Subtask subtaskUpdate = subtasks.get(subtask.getId());
        prioritizedTasks.remove(subtask);
        subtaskUpdate.setName(subtask.getName());
        subtaskUpdate.setStartTime(subtask.getStartTime());
        subtaskUpdate.setDuration(subtask.getDuration());
        subtaskUpdate.setDescription(subtask.getDescription());
        subtaskUpdate.setStatus(subtask.getStatus());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpic(epic);
        add(subtask);

    }
    @Override
    public void updateEpic(Epic epic) throws IOException, ManagerSaveException {

        Epic epicUpdate = epics.get(epic.getId());
        calculateStatus(epic.getId());
        epicUpdate.setName(epic.getName());
        epicUpdate.setDescription(epic.getDescription());
        List<Integer> idList = epic.getSubtaskId();
        int newEpicDuration = 0;
        for (Integer idSub : idList) {

            Subtask subtask = subtasks.get(idSub);

            if(subtask.getStartTime() != null) {

                if (epic.getStartTime() == null || subtask.getStartTime().isBefore(epic.getStartTime()) ||
                        subtask.getStartTime().equals(epic.getStartTime())) {
                    epic.setStartTime(subtask.getStartTime());
                }

                if (epic.getEndTime() == null || subtask.getStartTime().plus(subtask.getDuration(), ChronoUnit.MINUTES).isAfter(epic.getEndTime())) {
                    epicUpdate.setEndTime(subtask.getStartTime().plus(subtask.getDuration(), ChronoUnit.MINUTES));
                }
            }
            newEpicDuration += subtask.getDuration();

        }
        epic.setDuration(newEpicDuration);
        epics.put(epic.getId(),epicUpdate);
    }
    @Override
    public void deleteTask(int id) throws IOException, ManagerSaveException {
        Task task = tasks.get(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
        tasks.remove(id);

    }
    @Override
    public void deleteSubtask(int id) throws IOException, ManagerSaveException {
        Subtask subtask = subtasks.get(id);
        prioritizedTasks.remove(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        List<Integer> checkList = epic.getSubtaskId();
        for(Integer epicFor : checkList) {
            if (epicFor.equals(id)) {
                epic.getSubtaskId().remove(epicFor);
                break;
            }
        }
        historyManager.remove(id);
        updateEpic(epic);
        subtasks.remove(id);
    }
    @Override
    public void deleteEpic(int id) throws IOException, ManagerSaveException {

        Epic epic = epics.get(id);
        List<Integer> idList = epic.getSubtaskId();

        for (Integer idSub : idList) {
            Subtask subtask = subtasks.get(idSub);
            prioritizedTasks.remove(subtask);
            subtasks.remove(idSub);
            historyManager.remove(idSub);
        }
        epics.remove(id);
        historyManager.remove(id);
    }
    @Override
    public void clearAllTasks() throws IOException, ManagerSaveException {
        for(Integer task : tasks.keySet()){
            historyManager.remove(task);
        }
        List<Task> tasks1 = getTasks();
        for(Task task : tasks1) {
            prioritizedTasks.remove(task);
        }
        tasks.clear();

    }
    @Override
    public  void clearAllSubtasks() throws IOException, ManagerSaveException {
        for (Integer subs : subtasks.keySet()) {
            historyManager.remove(subs);
            Subtask subtask = subtasks.get(subs);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskId().clear();
            updateEpic(epic);
        }

        List<Subtask> subtasks1 = getSubtasks();
        for(Subtask sub : subtasks1) {
            prioritizedTasks.remove(sub);
        }
        subtasks.clear();
    }
    @Override
    public void clearAllEpics() throws IOException, ManagerSaveException {
        for(Integer subs : subtasks.keySet()){
            historyManager.remove(subs);
        }

        for(Integer epic : epics.keySet()){
            historyManager.remove(epic);
        }

        List<Subtask> subtasks1 = getSubtasks();
        for(Task sub : subtasks1) {
            prioritizedTasks.remove(sub);
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
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void add(Task task){

        if(task.getStartTime() == null){
            prioritizedTasks.add(task);
            return;
         }
        for(Task task1 : prioritizedTasks) {

            if (task1.getStartTime() != null) {
                if (task1.getStartTime().equals(task.getStartTime()) ||
                        (task1.getStartTime().isBefore(task.getEndTime()) && task1.getEndTime().isAfter(task.getStartTime()))) {

                    System.out.println("Пересечение задач по времни");
                    return;
                }
            }
        }
        prioritizedTasks.add(task);
    }
}
