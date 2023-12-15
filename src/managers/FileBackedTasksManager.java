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
        TaskManager saveManager = Managers.getDefault(file);

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один");
        Task task2 = new Task("Task 2", Status.NEW, "Таск второй");

        final int taskId1 = saveManager.createTask(task1);
        final int taskId2 = saveManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", Status.NONE, "Эпик первый");
        Epic epic2 = new Epic("Epic 2",Status.NONE, "Эпик второй");

        final int epicId1 = saveManager.createEpic(epic1);
        final int epicId2 = saveManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", epicId1);
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", epicId1);
        Subtask subtask3 = new Subtask("SubTask 3", Status.NEW, "Первая подзадача второго эпика", epicId1);

        final int subtaskId1 = saveManager.createSubtask(subtask1);
        final int subtaskId2 = saveManager.createSubtask(subtask2);
        final int subtaskId3 = saveManager.createSubtask(subtask3);

        saveManager.getTask(taskId2);
        saveManager.getEpic(epicId2);
        saveManager.getEpic(epicId1);
        saveManager.getEpic(epicId2);
        saveManager.getSubtask(subtaskId2);
        saveManager.getSubtask(subtaskId2);
        saveManager.getSubtask(subtaskId3);
        saveManager.getSubtask(subtaskId1);
        saveManager.getSubtask(subtaskId2);
        System.out.println(saveManager.getEpics());
        System.out.println(saveManager.getEpics());
        System.out.println(saveManager.getSubtasks());
        System.out.println(saveManager.getHistory());
        System.out.println("-----");

        TaskManager fileBackedTasksManager = loadFromFile(file);

        System.out.println(fileBackedTasksManager.getEpics());
        System.out.println(fileBackedTasksManager.getTasks());
        System.out.println(fileBackedTasksManager.getSubtasks());
        System.out.println(fileBackedTasksManager.getHistory());

        System.out.println(saveManager.getTasks().equals(fileBackedTasksManager.getTasks()));
        System.out.println(saveManager.getSubtasks().equals(fileBackedTasksManager.getSubtasks()));
        System.out.println(saveManager.getEpics().equals(fileBackedTasksManager.getEpics()));
        System.out.println(saveManager.getHistory().equals(fileBackedTasksManager.getHistory()));
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
                bufferedWriter.write(FormatterUtil.toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                bufferedWriter.write(FormatterUtil.toString(epic) + "\n");
            }
            for (Subtask sub : getSubtasks()) {
                Subtask subtask = subtasks.get(sub.getId());
                bufferedWriter.write(FormatterUtil.toString(sub) + "," + subtask.getEpicId() + "\n");
            }
            bufferedWriter.write("\n" + FormatterUtil.historyToString(historyManager));
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
                FormatterUtil.fromString(value, taskManager);
            }
            while (br.ready()) {
                String value = br.readLine();
                if (!value.isEmpty()) {
                    FormatterUtil.historyFromString(value, taskManager);
                }
            }
        } catch (IOException exc) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
        }
        return taskManager;
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
