import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    final protected HashMap<Integer, Task> tasks = new HashMap<>();
    final protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    final protected HashMap<Integer, Epic> epics = new HashMap<>();

    private int idGenerator = 0;

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskArrayList = new ArrayList<>(tasks.values());
        return taskArrayList;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>(subtasks.values());
        return subtaskArrayList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicArrayList = new ArrayList<>(epics.values());
        return epicArrayList;
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
            calculateStatus(epic.getId());
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
        epics.remove(id);
    }

    public void clearAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    String calculateStatus(int epicId) {

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

