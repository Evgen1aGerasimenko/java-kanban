package tests;

import exceptions.ManagerSaveException;
import managers.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    KVServer server;
    private Task task;
    private Subtask subtask;
    private Epic epic;

    @BeforeEach
    public void beforeEach() throws IOException, ManagerSaveException {
            server = new KVServer();
            server.start();
            taskManager = new HttpTaskManager(8078);
    }


    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @DisplayName("Проверка загрузки из HttpTaskManager")
    @Test
    public void shouldLoadFromHttpServer() throws IOException, ManagerSaveException {
        task = new Task("Task 1", Status.NEW, "Таск номер один", 1, LocalDateTime.now().plusMinutes(10));
        taskManager.createTask(task);
        epic = new Epic("Epic 1", "Эпик первый");
        final int epicId = taskManager.createEpic(epic);
        subtask = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 1, LocalDateTime.now(), epicId);
        taskManager.createSubtask(subtask);

        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.getEpic(epic.getId());

        taskManager = new HttpTaskManager(8078,true);

        final List<Task> tasks = taskManager.getTasks();
        final List<Subtask> subtasks = taskManager.getSubtasks();
        final List<Epic> epics = taskManager.getEpics();
        final List<Task> history = taskManager.getHistory();

        assertFalse(tasks.isEmpty(),"Список задач пуст");
        assertFalse(subtasks.isEmpty(),"Список подзадач пуст");
        assertFalse(epics.isEmpty(),"Список эпиков пуст");
        assertFalse(history.isEmpty(),"Список истории пуст");

        assertEquals(1, tasks.size(), "Список должен содержать одну задачу");
        assertEquals(1, subtasks.size(), "Список должен содержать одну подзадачу");
        assertEquals(1, epics.size(), "Список должен содержать один эпик");
        assertEquals(3, history.size(), "Список должен содержать три записи истории");
    }

    @DisplayName("Проверка сохранения в HttpTaskManager")
    @Test
    public void shouldSave() throws IOException, ManagerSaveException {
        task = new Task("Task 1", Status.NEW, "Таск номер один", 1, LocalDateTime.now().plusMinutes(10));
        taskManager.createTask(task);
        epic = new Epic("Epic 1", "Эпик первый");
        final int epicId = taskManager.createEpic(epic);
        subtask = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 1, LocalDateTime.now(), epicId);
        taskManager.createSubtask(subtask);

        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask.getId());
        taskManager.getEpic(epic.getId());

        assertEquals(task, taskManager.getTask(1), "Задача не сохранена");
        assertEquals(subtask, taskManager.getSubtask(3), "Подзадача не сохранена");
        assertEquals(epic, taskManager.getEpic(2), "Эпик не сохранен");

        final List<Task> history = taskManager.getHistory();
        assertTrue(history.contains(task), "История не содержит задачу");
        assertTrue(history.contains(subtask), "История не содержит подзадачу");
        assertTrue(history.contains(epic), "История не содержит эпик");
    }
}
