package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public static void main(String[] args) throws ManagerSaveException {
        File file = new File("src/resources/file.csv");
        FileBackedTasksManager f = new FileBackedTasksManager(file);

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один");
        Task task2 = new Task("Task 2", Status.NEW, "Таск второй");

        final int taskId1 = f.createTask(task1);
        final int taskId2 = f.createTask(task2);

        Epic epic1 = new Epic("Epic 1", Status.NONE, "Эпик первый");
        Epic epic2 = new Epic("Epic 2",Status.NONE, "Эпик второй");

        final int epicId1 = f.createEpic(epic1);
        final int epicId2 = f.createEpic(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", epicId1);
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", epicId1);
        Subtask subtask3 = new Subtask("SubTask 3", Status.NEW, "Первая подзадача второго эпика", epicId1);

        final int subtaskId1 = f.createSubtask(subtask1);
        final int subtaskId2 = f.createSubtask(subtask2);
        final int subtaskId3 = f.createSubtask(subtask3);


        f.getTask(taskId2);
        System.out.println(f.getHistory());
        f.getEpic(epicId2);
        System.out.println(f.getHistory());
        f.getEpic(epicId1);
        System.out.println(f.getHistory());
        f.getEpic(epicId2);
        System.out.println(f.getHistory());
        f.getSubtask(subtaskId2);
        System.out.println(f.getHistory());
        f.getSubtask(subtaskId2);
        f.getSubtask(subtaskId3);
        f.getSubtask(subtaskId1);
        System.out.println(f.getHistory());
        f.getSubtask(subtaskId2);
        System.out.println(f.getHistory());
        System.out.println("----------");

        FileBackedTasksManager fileBackedTasksManager = loadFromFile(file);
        System.out.println(fileBackedTasksManager.tasks);
        System.out.println(fileBackedTasksManager.subtasks);
        System.out.println(fileBackedTasksManager.epics);
        System.out.println(fileBackedTasksManager.getHistory());
    }

    protected final File file;

    public FileBackedTasksManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }

    protected void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {

            bufferedWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                bufferedWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                bufferedWriter.write(toString(epic) + "\n");
            }
            for (Subtask sub : getSubtasks()) {
                Subtask subtask = subtasks.get(sub.getId());
                bufferedWriter.write(toString(sub) + "," + subtask.getEpicId() + "\n");
            }
            bufferedWriter.write("\n" + historyToString(historyManager));
        } catch (IOException exc) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }


    public static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            while (br.ready()) {
                String value = br.readLine();
                if (value.isEmpty()) {
                    break;
                }
                fromString(value, taskManager);
            }
            while (br.ready()) {
                String value = br.readLine();
                if (!value.isEmpty()) {
                    historyFromString(value, taskManager);
                }
            }
        } catch (IOException exc) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return taskManager;
    }


    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription();
    }

    private static Task fromString(String value, FileBackedTasksManager fileBackedTasksManager) {
        final String[] values = value.split(",");
        TaskType taskType = TaskType.valueOf(values[1]);
        final int id = Integer.parseInt(values[0]);
        String name = values[2];
        Status status = Status.valueOf(values[3]);
        String description = values[4];
        Task task;
        switch (taskType) {
            case TASK:
                task = new Task(name, status, description);
                task.setId(id);
                fileBackedTasksManager.tasks.put(id, task);
                return task;
            case SUBTASK:
                final int EpicId = Integer.parseInt(values[5]);
                task = new Subtask(name, status, description, EpicId);
                task.setId(id);
                fileBackedTasksManager.subtasks.put(id, (Subtask) task);
                return task;
            case EPIC:
                task = new Epic(name, status, description);
                task.setId(id);
                fileBackedTasksManager.epics.put(id, (Epic) task);
                return task;
        }
        return null;
    }

    private static String historyToString(HistoryManager manager) {
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

    private static List<Integer> historyFromString(String value, FileBackedTasksManager fileBackedTasksManager) throws ManagerSaveException {
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

    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public List<Subtask> getSubtasksOfTasks(int epicId) {
        return super.getSubtasksOfTasks(epicId);
    }

    @Override
    public Task getTask(int id) throws ManagerSaveException {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(int id) throws ManagerSaveException {
        final Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpic(int id) throws ManagerSaveException {
        final Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public int createTask(Task task) throws ManagerSaveException {
        save();
        return super.createTask(task);
    }

    @Override
    public int createSubtask(Subtask subtask) throws ManagerSaveException {
        save();
        return super.createSubtask(subtask);
    }

    @Override
    public int createEpic(Epic epic) throws ManagerSaveException {
        save();
        return super.createEpic(epic);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void deleteTask(int id) throws ManagerSaveException {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) throws ManagerSaveException {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) throws ManagerSaveException {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void clearAllTasks() throws ManagerSaveException {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllSubtasks() throws ManagerSaveException {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void clearAllEpics() throws ManagerSaveException {
        super.clearAllEpics();
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
