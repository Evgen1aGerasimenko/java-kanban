
import exceptions.ManagerSaveException;
import managers.*;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
public class Main {

    public static void main(String[] args) throws IOException, ManagerSaveException {

        new KVServer().start();
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 1, LocalDateTime.now().plusSeconds(120));
        Task task2 = new Task("Task 2", Status.NEW, "Таск второй",1, LocalDateTime.now().plusSeconds(40));

       final int taskId1 = taskManager.createTask(task1);
       final int taskId2 = taskManager.createTask(task2);


        Epic epic1 = new Epic("Epic 1","Эпик первый");
        Epic epic2 = new Epic("Epic 2", "Эпик второй");

        final int epicId1 = taskManager.createEpic(epic1);
        final int epicId2 = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 1, null, epicId1);
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", 1, LocalDateTime.now().plusSeconds(200), epicId1);
        Subtask subtask3 = new Subtask("SubTask 3", Status.NEW, "Первая подзадача второго эпика", 1, null, epicId1);

        final int subtaskId1 = taskManager.createSubtask(subtask1);
        final int subtaskId2 = taskManager.createSubtask(subtask2);
        final int subtaskId3 = taskManager.createSubtask(subtask3);

    }
}
