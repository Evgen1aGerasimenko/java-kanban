package tests;
import exceptions.ManagerSaveException;
import managers.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract public class TaskManagerTest<T extends TaskManager> {

    T taskManager;

   public void init() throws IOException, ManagerSaveException {

       Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 1, LocalDateTime.now().plusSeconds(120));
       Task task2 = new Task("Task 2", Status.NEW, "Таск второй",1, LocalDateTime.now().plusSeconds(40));

       final int taskId1 = taskManager.createTask(task1);
       final int taskId2 = taskManager.createTask(task2);

       Epic epic1 = new Epic("Epic 1", Status.NONE, "Эпик первый", 1, LocalDateTime.now().plusSeconds(60), null);
       Epic epic2 = new Epic("Epic 2",Status.NONE, "Эпик второй", 1, LocalDateTime.now().plusSeconds(80), LocalDateTime.now().plusSeconds(20));

       final int epicId1 = taskManager.createEpic(epic1);
       final int epicId2 = taskManager.createEpic(epic2);

       Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 1, null, epicId1);
       Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", 1, LocalDateTime.now().plusSeconds(200),epicId1);
       Subtask subtask3 = new Subtask("SubTask 3", Status.NEW, "Первая подзадача второго эпика", 1, null,epicId1);

       final int subtaskId1 = taskManager.createSubtask(subtask1);
       final int subtaskId2 = taskManager.createSubtask(subtask2);
       final int subtaskId3 = taskManager.createSubtask(subtask3);
   }

    @DisplayName("Получаем список задач")
    @Test
    void shouldReturnListOfTasks() throws IOException, ManagerSaveException {
        assertEquals(Collections.emptyList(), taskManager.getTasks(), "Список задач не пуст");
        init();
        final List<Task> tasks = taskManager.getTasks();
        assertFalse(taskManager.getTasks().isEmpty(), "Список задач пуст");
        assertFalse(taskManager.getTasks().contains(taskManager.getTask(100)), "Список содержит задачу с неверным идентификатором");
        assertTrue(taskManager.getTasks().contains(taskManager.getTask(1)), "Список не содержит добавленную задачу");
        assertEquals(2, tasks.size(), "В списке неверное количество элементов");
    }

    @DisplayName("Получаем список подзадач")
    @Test
    void shouldReturnListOfSubtasks() throws IOException, ManagerSaveException {
        assertEquals(Collections.emptyList(), taskManager.getSubtasks(), "Список подзадач не пуст");
        init();
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertFalse(taskManager.getSubtasks().isEmpty(), "Список подзадач пуст");
        assertFalse(taskManager.getSubtasks().contains(taskManager.getSubtask(100)), "Список содержит подзадачу с неверным идентификатором");
        assertTrue(taskManager.getSubtasks().contains(taskManager.getSubtask(7)), "Список не содержит добавленную подзадачу");
        assertEquals(3, subtasks.size(), "В списке неверное количество элементов");
    }

    @DisplayName("Получаем список эпиков")
    @Test
    void shouldReturnListOfEpics() throws IOException, ManagerSaveException {
        assertEquals(Collections.emptyList(), taskManager.getEpics(), "Список эпиков не пуст");
        init();
        final List<Epic> epics = taskManager.getEpics();
        assertFalse(taskManager.getEpics().isEmpty(), "Список эпиков пуст");
        assertFalse(taskManager.getEpics().contains(taskManager.getEpic(100)), "Список содержит эпик с неверным идентификатором");
        assertTrue(taskManager.getEpics().contains(taskManager.getEpic(3)), "Список не содержит добавленный эпик");
        assertEquals(2, epics.size(), "В списке неверное количество элементов");
    }

    @DisplayName("Получаем подзадачи эпика по идентификатору")
    @Test
    void shouldReturnSubtaskByEpicId() throws IOException, ManagerSaveException {
       assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач не пуст");

        Epic epic = new Epic("EpicTest", Status.NEW, "Epic create test", 10, LocalDateTime.now(), null);
        final int epicId = taskManager.createEpic(epic);

        Epic epic1 = new Epic("EpicTest1", Status.NEW, "Epic create test1", 10, LocalDateTime.now(), null);
        final int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask = new Subtask("SubtaskTest", Status.NEW, "Subtask create test for epic", 10, LocalDateTime.now(),epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        Subtask subtask1 = new Subtask("SubtaskTest1", Status.NEW, "Subtask create test1 for epic1", 10, LocalDateTime.now().plus(20, ChronoUnit.MINUTES),epicId);
        final int subtaskId1 = taskManager.createSubtask(subtask1);

        List<Subtask> newSub = new ArrayList<>();
        List<Subtask> subtasks = taskManager.getSubtasks();
        for(Subtask subs : subtasks) {
            if(subs.getEpicId() == epicId) {
                newSub.add(subs);
            }
        }
         assertEquals(newSub, taskManager.getSubtasksOfTasks(epicId), "Список сабтасков эпика не совпадает с ожидаемым");
         assertEquals(2, newSub.size(), "Длинна списка подзадач отлична от ожидаемой");
         assertNotEquals(newSub, taskManager.getSubtasksOfTasks(epicId1), "Список сабтасков эпика не должен включать данные подзадачи");
    }

    @DisplayName("Получаем задачу по идентификатору, добавляем в историю просмотров")
    @Test
    void shouldReturnTaskById() throws IOException, ManagerSaveException {

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 10, LocalDateTime.now());
        final int taskId1 = taskManager.createTask(task1);

        assertEquals(task1, taskManager.getTask(taskId1), "Задачи не совпадают");
        assertNotNull(taskManager.getTask(taskId1), "Невозможно получить задачу по идентификатору");
        assertNull(taskManager.getTask(20), "Такая задача не должна существовать");

    }

    @DisplayName("Получаем подзадачу по идентификатору, добавляем в историю просмотров")
    @Test
    void shouldReturnSubtaskById() throws IOException, ManagerSaveException {
        Epic epic = new Epic("EpicTest", Status.NEW, "Epic create test", 10, LocalDateTime.now(), null);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("SubtaskTest", Status.NEW, "Subtask create test", 10, LocalDateTime.now(), epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtask(subtaskId), "Подзадачи не совпадают");
        assertNotNull(taskManager.getSubtask(subtaskId), "Невозможно получить подзадачу по идентификатору");
        assertNull(taskManager.getSubtask(20), "Такая подзадача не должна существовать");
    }

    @DisplayName("Получаем эпик по идентификатору, добавляем в историю просмотров")
    @Test
    void shouldReturnEpicById() throws IOException, ManagerSaveException {
        Epic epic = new Epic("EpicTest", Status.NEW, "Epic create test", 10, LocalDateTime.now(), null);
        final int epicId = taskManager.createEpic(epic);

        assertEquals(epic, taskManager.getEpic(epicId), "Эпики не совпадают");
        assertNotNull(taskManager.getEpic(epicId), "Невозможно получить эпик по идентификатору");
        assertNull(taskManager.getEpic(20), "Такой эпик не должен существовать");
    }

    @DisplayName("Создаем таску")
    @Test
    void shouldCreateTask() throws IOException, ManagerSaveException {
        Task task = new Task("TaskTest", Status.NEW, "Task create test", 10, LocalDateTime.now());
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
        assertTrue(taskManager.getPrioritizedTasks().contains(task), "Список сортировки не содержит задачи");
        assertTrue(task.getEndTime().equals(task.getStartTime().plus(task.getDuration(), ChronoUnit.MINUTES)),
                "Неверное время завешения задачи");
    }

    @DisplayName("Создаем сабтаск")
    @Test
    void shouldCreateSubtask() throws IOException, ManagerSaveException {
        Epic epic = new Epic("EpicTest", Status.NEW, "Epic create test", 10, LocalDateTime.now(), null);
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("SubtaskTest", Status.NEW, "Subtask create test", 10, LocalDateTime.now(), epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
        assertTrue(taskManager.getPrioritizedTasks().contains(subtask), "Список сортировки не содержит подзадачи");
        assertTrue(taskManager.getEpic(epicId).getSubtaskId().contains(subtaskId),  "У подзадачи нет эпика");

        assertTrue(subtask.getEndTime().equals(subtask.getStartTime().plus(subtask.getDuration(), ChronoUnit.MINUTES)),
                "Неверное время завешения подзадачи");
    }

    @DisplayName("Создаем эпик")
    @Test
    void shouldCreateEpic() throws IOException, ManagerSaveException {
        Epic epic = new Epic("EpicTest", "Epic create test");
        final int epicId = taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");
        assertFalse(taskManager.getPrioritizedTasks().contains(epic), "Список сортировки содержит эпик");

        Subtask subtask = new Subtask("SubtaskTest", Status.NEW, "Subtask create test", 10, LocalDateTime.now(), epicId);
        final int subtaskId = taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("SubtaskTest", Status.NEW, "Subtask create test", 10, LocalDateTime.now().plus(20,ChronoUnit.MINUTES),epicId);
        final int subtaskId2 = taskManager.createSubtask(subtask2);

        assertTrue(epic.getStartTime().equals(subtask.getStartTime()), "Неверное время начала эпика");
        assertTrue(epic.getEndTime().equals(subtask2.getEndTime()), "Неверное время завешения эпика");
        assertEquals(20, epic.getDuration(), "Неверная длительность эпика");

    }

    @DisplayName("Обновление задачи")
    @Test
    void shouldUpdateTask() throws IOException, ManagerSaveException {
        Task task = new Task("TaskTest", Status.NEW, "Task create test", 10, LocalDateTime.now());
        final int taskId = taskManager.createTask(task);

        Task task2 = new Task("Task 2", Status.DONE, "Обновленная задача", 10, LocalDateTime.now().plus(40,ChronoUnit.MINUTES));
        final int taskId2 = taskManager.createTask(task2);

        List<Task> priorTaskList = taskManager.getPrioritizedTasks();
        task.setStartTime(null);
        taskManager.updateTask(task);

        assertTrue(task.getStartTime() == null, "Задаче не был присвоен null");
        assertNotEquals(taskManager.getPrioritizedTasks(), priorTaskList,
                "Списки равны, задача после обновления не поменяла своего места в списке");
        task = task2;
        taskManager.updateTask(task);
        assertTrue(task.getName().equals("Task 2"), "Задаче не было присвоено новое имя");
        assertTrue(task.getStatus().equals(Status.DONE), "Задаче не был присвоенон новый статус");
        assertTrue(task.getDescription().equals("Обновленная задача"), "Задаче не было присвоено новое описание");
        assertTrue(task.getDuration() == task2.getDuration(), "Задаче не была присвоена новая длительность");
        assertTrue(task.getStartTime().equals(task2.getStartTime()), "Задаче не было присвоено новое время начала");
        assertTrue(task.getEndTime().equals(task2.getEndTime()), "Задаче не было присвоено новое время окончания");
    }

    @DisplayName("Обновление времени начала, завершения и длительности эпика")
    @Test
    void shouldUpdateEpicsStartAndEnDTimePlusDuration() throws IOException, ManagerSaveException {
        Epic epic1 = new Epic("Epic 1", "Эпик первый");
        final int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 1, LocalDateTime.now().plusSeconds(100), epicId1);
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", 1, LocalDateTime.now().plusSeconds(200), epicId1);
        Subtask subtask3 = new Subtask("SubTask 3", Status.NEW, "Первая подзадача второго эпика", 1, LocalDateTime.now().plusSeconds(300), epicId1);

        final int subtaskId1 = taskManager.createSubtask(subtask1);
        final int subtaskId2 = taskManager.createSubtask(subtask2);
        final int subtaskId3 = taskManager.createSubtask(subtask3);

        assertEquals(subtask1.getStartTime(), epic1.getStartTime(),
                "Время начала эпика не совпадает со временем начала самой ранней подзадачи");
        assertEquals(subtask3.getEndTime(), epic1.getEndTime(),
                "Время завершения эпика не совпадает со временем завершения самой поздней подзадачи ");
        assertEquals(3, epic1.getDuration(), "Продолжительность эпика не равна сумме всех подзадач");
        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Неверный статус эпика");
    }

    @DisplayName("Обновление подзадачи")
    @Test
    void shouldUpdateSubtask() throws IOException, ManagerSaveException {
        Epic epic1 = new Epic("Epic 1", Status.NONE, "Эпик первый", 1, null, null);
        final int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 1, LocalDateTime.now().plusSeconds(100), epicId1);
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", 1, LocalDateTime.now().plusSeconds(200), epicId1);
        Subtask subtask3 = new Subtask("SubTask 3", Status.NEW, "Первая подзадача второго эпика", 1, LocalDateTime.now().plusSeconds(300), epicId1);

        final int subtaskId1 = taskManager.createSubtask(subtask1);
        final int subtaskId2 = taskManager.createSubtask(subtask2);
        final int subtaskId3 = taskManager.createSubtask(subtask3);

        List<Task> priorTaskList = taskManager.getPrioritizedTasks();
        subtask1.setStartTime(null);
        taskManager.updateSubtask(subtask1);

        assertTrue(subtask1.getStartTime() == null, "Подзадаче не был присвоен null");
        assertNotEquals(taskManager.getPrioritizedTasks(), priorTaskList,
                "Списки равны, подзадача после обновления не поменяла своего места в списке");

        subtask1 = subtask2;
        taskManager.updateSubtask(subtask1);
        assertTrue(subtask1.getName().equals(subtask2.getName()), "Подзадаче не было присвоено новое имя");
        assertTrue(subtask1.getStatus().equals(Status.NEW), "Подзадаче не был присвоенон новый статус");
        assertTrue(subtask1.getDescription().equals("Подзадача эпика вторая"), "Подзадаче не было присвоено новое описание");
        assertTrue(subtask1.getDuration() == subtask2.getDuration(), "Подзадаче не была присвоена новая длительность");
        assertTrue(subtask1.getStartTime().equals(subtask2.getStartTime()), "Подзадаче не было присвоено новое время начала");
        assertTrue(subtask1.getEndTime().equals(subtask2.getEndTime()), "Подзадаче не было присвоено новое время окончания");
    }


    @DisplayName("Удаление задачи по идентификатору")
    @Test
    void shouldDeleteTaskById() throws IOException, ManagerSaveException {
        assertTrue(taskManager.getTasks().isEmpty(), "Список не пуст");
        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 10, LocalDateTime.now());
        final int taskId1 = taskManager.createTask(task1);
        assertNotNull(taskManager.getTasks().contains(task1), "Список пуст");

        taskManager.deleteTask(taskId1);
        assertFalse(taskManager.getPrioritizedTasks().contains(task1), "Этот список не должен содержать эту задачу");
        assertFalse(taskManager.getTasks().contains(task1), "Этот список не должен содержать эту задачу");
    }

    @DisplayName("Удаление подзадачи по идентификатору")
    @Test
    void shouldDeleteSubtaskById() throws IOException, ManagerSaveException {
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список не пуст");
        Epic epic = new Epic("EpicTest", Status.NEW, "Epic create test", 10, LocalDateTime.now(), null);
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("SubtaskTest", Status.NEW, "Subtask create test", 10, LocalDateTime.now(), epicId);
        final int subtaskId = taskManager.createSubtask(subtask);
        assertNotNull(taskManager.getSubtasks().contains(subtaskId), "Список пуст");

        taskManager.deleteSubtask(subtaskId);
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask), "Этот список не должен содержать эту подзадачу");
        assertFalse(taskManager.getSubtasks().contains(subtask), "Подзадача не удалена из списка подзадач");
        assertFalse(epic.getSubtaskId().contains(subtaskId), "Подзадача не удалена из эпика");
    }

    @DisplayName("Удаление эпика по идентификатору")
    @Test
    void shouldDeleteEpicById() throws IOException, ManagerSaveException {
        assertTrue(taskManager.getEpics().isEmpty(), "Список не пуст");
        Epic epic = new Epic("EpicTest", Status.NEW, "Epic create test", 10, LocalDateTime.now(), null);
        final int epicId = taskManager.createEpic(epic);
        assertNotNull(taskManager.getEpics().contains(epicId), "Список пуст");

        Subtask subtask = new Subtask("SubtaskTest", Status.NEW, "Subtask create test", 10, LocalDateTime.now(),epicId);
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.deleteEpic(epicId);

        assertFalse(taskManager.getPrioritizedTasks().contains(epic), "Этот список не должен содержать этот эпик");
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask), "Этот список не должен содержать эту подзадачу");

        assertFalse(taskManager.getEpics().contains(epic), "Эпик не удален из списка эпиков");
        assertFalse(taskManager.getSubtasks().contains(subtask), "Подзадача не удалена из списка подзадач");
    }

    @DisplayName("Очистка списка задач, удаление из истории и из списка сортировки")
    @Test
    void shouldClearAllTasks() throws IOException, ManagerSaveException {
        assertTrue(taskManager.getTasks().isEmpty(), "Список не пуст");
        init();
        List<Task> tasksList = taskManager.getTasks();
        assertNotNull(taskManager.getTasks(), "Список задач пуст");
        taskManager.clearAllTasks();
        assertFalse(taskManager.getPrioritizedTasks().containsAll(tasksList), "Этот список не должен содержать задач");
        assertEquals(Collections.emptyList(), taskManager.getTasks(), "Список задач не пуст");

    }

    @DisplayName("Очистка списка подзадач")
    @Test
    void shouldClearAllSubtasks() throws IOException, ManagerSaveException {
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список не пуст");
        init();
        List<Subtask> subtaskList = taskManager.getSubtasks();
        assertNotNull(taskManager.getSubtasks(), "Список подзадач пуст");
        taskManager.clearAllSubtasks();
        assertFalse(taskManager.getPrioritizedTasks().containsAll(subtaskList), "Этот список не должен содержать подзадач");
        assertEquals(Collections.emptyList(), taskManager.getSubtasks(), "Список подзадач не пуст");
        assertFalse(taskManager.getEpics().containsAll(subtaskList), "Эпики содержат подзадачи");
    }

    @DisplayName("Очистка списка эпиков")
    @Test
    void shouldClearAllEpics() throws IOException, ManagerSaveException {
        assertTrue(taskManager.getEpics().isEmpty(), "Список не пуст");
        init();
        List<Subtask> subtaskList = taskManager.getSubtasks();
        assertNotNull(taskManager.getEpics(), "Список эпиков пуст");
        assertNotNull(taskManager.getSubtasks(), "Список подзадач пуст");
        taskManager.clearAllEpics();
        assertFalse(taskManager.getPrioritizedTasks().containsAll(subtaskList), "Этот список не должен содержать подзадач");
        assertEquals(Collections.emptyList(), taskManager.getEpics(), "Список эпиков не пуст");
        assertEquals(Collections.emptyList(), taskManager.getSubtasks(), "Список подзадач не пуст");
    }

    @DisplayName("Получение истории")
    @Test
    void shouldReturnHistory() throws IOException {
        //тут всегда пустая история
        assertEquals(Collections.emptyList(), taskManager.getHistory(), "История должна быть пустой");
    }

    @DisplayName("Список, возвращающий отсортированные задачи")
    @Test
    void shouldReturnPrioritizedTasks() throws ManagerSaveException, IOException {
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Список не пуст");
        init();

        assertFalse(taskManager.getPrioritizedTasks().isEmpty(), "Пустой список");
        assertTrue(taskManager.getPrioritizedTasks().containsAll(taskManager.getTasks()), "Список содержит не все задачи");
        assertTrue(taskManager.getPrioritizedTasks().containsAll(taskManager.getSubtasks()), "Список содержит не все подзадачи");
        assertFalse(taskManager.getPrioritizedTasks().containsAll(taskManager.getEpics()), "Список не должен содержать эпики");

    }
}
