import exceptions.ManagerSaveException;
import managers.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Instant;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {

        File file = new File("src/resources/file.csv");
        TaskManager taskManager = Managers.getDefault(file);

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 1, Instant.now().plusSeconds(120));
        Task task2 = new Task("Task 2", Status.NEW, "Таск второй",1, Instant.now().plusSeconds(40));

       final int taskId1 = taskManager.createTask(task1);
       final int taskId2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", Status.NONE, "Эпик первый", 1, Instant.now().plusSeconds(60), null);
        Epic epic2 = new Epic("Epic 2",Status.NONE, "Эпик второй", 1, Instant.now().plusSeconds(80), Instant.now().plusSeconds(20));

        final int epicId1 = taskManager.createEpic(epic1);
        final int epicId2 = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 1, null, epicId1);
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", 1, Instant.now().plusSeconds(200), epicId1);
        Subtask subtask3 = new Subtask("SubTask 3", Status.NEW, "Первая подзадача второго эпика", 1, null, epicId1);

        final int subtaskId1 = taskManager.createSubtask(subtask1);
        final int subtaskId2 = taskManager.createSubtask(subtask2);
        final int subtaskId3 = taskManager.createSubtask(subtask3);

    }
}
