import managers.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один");
        Task task2 = new Task("Task 2", Status.NEW, "Таск второй");

       final int taskId1 = taskManager.createTask(task1);
       final int taskId2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", Status.NONE, "Эпик первый");
        Epic epic2 = new Epic("Epic 2",Status.NONE, "Эпик второй");

        final int epicId1 = taskManager.createEpic(epic1);
        final int epicId2 = taskManager.createEpic(epic2);

        System.out.println(epic1);
        System.out.println(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", epicId1);
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", epicId1);
        Subtask subtask3 = new Subtask("SubTask 3", Status.NEW, "Первая подзадача второго эпика", epicId1);

        final int subtaskId1 = taskManager.createSubtask(subtask1);
        final int subtaskId2 = taskManager.createSubtask(subtask2);
        final int subtaskId3 = taskManager.createSubtask(subtask3);



      taskManager.getEpic(taskId1);
        System.out.println(taskManager.getHistory());
      taskManager.getTask(taskId2);
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epicId2);
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epicId1);
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epicId2);
        System.out.println(taskManager.getHistory());
      taskManager.getSubtask(subtaskId2);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtask(subtaskId2);
        taskManager.getSubtask(subtaskId3);
        taskManager.getSubtask(subtaskId1);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtask(subtaskId2);
        System.out.println(taskManager.getHistory());
        taskManager.deleteEpic(epicId2);
        taskManager.deleteEpic(epicId1);
        System.out.println(taskManager.getHistory());
        //taskManager.clearAllEpics();
       // System.out.println(taskManager.getHistory());
    }
}
