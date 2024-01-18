package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FormatterUtil {
    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() +
                "," + task.getDescription() + "," + task.getDuration() + "," + task.getStartTime() + "," + task.getEndTime();
    }

    protected static Task fromString(String value, FileBackedTasksManager fileBackedTasksManager) {
        final String[] values = value.split(",");
        TaskType taskType = TaskType.valueOf(values[1]);
        final int id = Integer.parseInt(values[0]);
        String name = values[2];
        Status status;
        if(values[3].equals("null")){
            status = null;
        } else {
            status = Status.valueOf(values[3]);
        }
        String description = values[4];
        final int duration = Integer.parseInt(values[5]);
        Instant startTime;
        Instant endTime;
        if(values[6].equals("null")){
            startTime = null;
        }else {
            startTime = Instant.parse(values[6]);
        }
        if(values[7].equals("null")){
            endTime = null;
        }else {
            endTime = Instant.parse(values[7]);
        }

        Task task;
        switch (taskType) {
            case TASK:
                task = new Task(name, status, description, duration, startTime, endTime);
                task.setId(id);
                fileBackedTasksManager.tasks.put(id, task);
                return task;
            case SUBTASK:
                final int EpicId = Integer.parseInt(values[8]);
                task = new Subtask(name, status, description, duration, startTime, endTime, EpicId);
                task.setId(id);
                Epic epic = fileBackedTasksManager.epics.get(((Subtask) task).getEpicId());
                epic.getSubtaskId().add(id);
                fileBackedTasksManager.subtasks.put(id, (Subtask) task);
                return task;
            case EPIC:
                task = new Epic(name, status, description, duration, startTime, endTime);
                task.setId(id);
                task.setEndTime(endTime);
                fileBackedTasksManager.epics.put(id, (Epic) task);
                return task;
        }
        return null;
    }

    protected static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            String s = String.valueOf(history.get(i).getId());
            sb.append(s).append(",");
            if (i == history.size() - 1) {
                sb.delete(sb.length() - 1, sb.length());
            }
        }
        return sb.toString();
    }

    protected static List<Integer> historyFromString(String value, FileBackedTasksManager fileBackedTasksManager) throws ManagerSaveException {
        final String[] idList = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String id : idList) {
            history.add(Integer.parseInt(id));
            for (var x : fileBackedTasksManager.tasks.entrySet()) {
                if (id.equals(String.valueOf(x.getKey()))) {
                    fileBackedTasksManager.getTask(Integer.parseInt(id));
                }
            }
            for (var x : fileBackedTasksManager.subtasks.entrySet()) {
                if (id.equals(String.valueOf(x.getKey()))) {
                    fileBackedTasksManager.getSubtask(Integer.parseInt(id));
                }
            }
            for (var x : fileBackedTasksManager.epics.entrySet()) {
                if (id.equals(String.valueOf(x.getKey()))) {
                    fileBackedTasksManager.getEpic(Integer.parseInt(id));
                }
            }
        }
        return history;
    }
}
