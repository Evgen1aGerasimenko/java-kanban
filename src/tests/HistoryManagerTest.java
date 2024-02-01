package tests;

import exceptions.ManagerSaveException;
import managers.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
    File file = new File("src/resources/file.csv");
    InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

    @DisplayName("Добавление в историю")
    @Test
    void shouldAddToHistory() throws IOException, ManagerSaveException {

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 10, LocalDateTime.now());
        final int taskId1 = taskManager.createTask(task1);

        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая");
        assertEquals(1,history.size(), "История пустая");
    }

    @DisplayName("Возвращаем историю")
    @Test
    void shouldReturnHistory() throws IOException, ManagerSaveException {
        assertEquals(Collections.emptyList(), taskManager.getHistory(), "История не пустая");

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 10, LocalDateTime.now());
        final int taskId1 = taskManager.createTask(task1);

        taskManager.getTask(taskId1);

        assertNotNull(taskManager.getHistory(), "Пустая история");
        assertTrue(taskManager.getHistory().contains(task1), "История не содержит просмотренную задачу");
        assertEquals(1, taskManager.getHistory().size(), "История содержит неверное количество элементов");


    }

    @DisplayName("Удаление из истории")
    @Test
    void shouldRemoveFromHistory() throws ManagerSaveException, IOException {
        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 10, LocalDateTime.now());
        final int taskId1 = taskManager.createTask(task1);

        Epic epic1 = new Epic("Epic 1", "Эпик первый");
        Epic epic2 = new Epic("Epic 2", "Эпик второй");
        final int epicId1 = taskManager.createEpic(epic1);
        final int epicId2 = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 10, LocalDateTime.now().plus(20, ChronoUnit.MINUTES), epicId1);
        final int subtaskId1 = taskManager.createSubtask(subtask1);

        taskManager.getTask(taskId1);
        taskManager.getEpic(epicId1);
        taskManager.getSubtask(subtaskId1);
        taskManager.getEpic(epicId2);

        assertEquals(4,taskManager.getHistory().size(), "В истории неверное количество элементов");

        taskManager.deleteSubtask(subtaskId1);
        assertFalse(taskManager.getHistory().contains(subtask1), "Некорректное удаление среднего элемента списка(подзадача)");

        taskManager.deleteEpic(epicId2);
        assertFalse(taskManager.getHistory().contains(epic2), "Некорректное удаление последнего элемента списка(эпик)");

        taskManager.deleteTask(taskId1);
        assertFalse(taskManager.getHistory().contains(task1), "Некорректное удаление первого элемента списка(задача)");
    }
}