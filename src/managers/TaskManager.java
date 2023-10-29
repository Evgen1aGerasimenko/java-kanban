package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();

    private int idGenerator = 0;

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasksOfTasks(int epicId) {
        ArrayList<Subtask> newSub = new ArrayList<>();
        ArrayList<Subtask> sub = getSubtasks();
        for (Subtask subtask : sub) {
            if (subtask.getEpicId() == epicId) {
                newSub.add(subtask);
            }
        }
        return newSub;
    }


    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public int createTask(Task task) {
        int id = ++idGenerator;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int createSubtask(Subtask subtask) {
        Epic epic = getEpic(subtask.getEpicId());
        if (epic != null) {
            int id = ++idGenerator;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.getSubtaskId().add(id);
            return id;
        } else {
            System.out.println("no such epic to create subtask" + subtask.getEpicId());
            return -1;
        }
    }

    public int createEpic(Epic epic) {
        int id = ++idGenerator;
        epic.setId(id);
        epics.put(id, epic);
        calculateStatus(epic.getId());
        return id;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Subtask subtaskUpdate = subtasks.get(subtask.getId());
            subtaskUpdate.setName(subtask.getName());
            Epic epic = getEpic(subtask.getEpicId());
            calculateStatus(epic.getId());
        } else {
            return;
        }
    }

    public void updateEpic(Epic epic) {
        Epic epicUpdate = epics.get(epic.getId());
        epicUpdate.setName(epic.getName());
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = getEpic(subtask.getEpicId());
        List<Integer> checkList = epic.getSubtaskId();
        for(Integer epicFor : checkList) {
            if (epicFor.equals(id)) {

                epic.getSubtaskId().remove(epicFor);
            }
        }

        calculateStatus(epic.getId());
        subtasks.remove(id);
    }

    public void deleteEpic(int id) {

        Epic epic = epics.get(id);
        List<Integer> idList = epic.getSubtaskId();

        for (Integer idSub : idList) {
                subtasks.remove(idSub);
        }
        epics.remove(id);
    }
    private String calculateStatus(int epicId) {

        Epic epic = epics.get(epicId);
        if (epic == null) return null;
        List<Integer> idList = epic.getSubtaskId();

        if (idList.isEmpty()) {
            return "NEW";
        }
        int newStatus = 0;
        int doneStatus = 0;
        int inProgressStatus = 0;
        for (Integer id : idList) {
            if (subtasks.get(id).getStatus().equals("NEW")) newStatus++;
            if (subtasks.get(id).getStatus().equals("DONE")) doneStatus++;
            if (subtasks.get(id).getStatus().equals("IN_PROGRESS")) inProgressStatus++;
        }
        if (newStatus > 0 && doneStatus == 0 && inProgressStatus == 0) {
            epic.setStatus("NEW");
        } else if (newStatus == 0 && doneStatus > 0 && inProgressStatus == 0) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
        return epic.getStatus();
    }
}

