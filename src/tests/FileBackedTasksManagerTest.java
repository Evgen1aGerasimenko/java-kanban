package tests;
import exceptions.ManagerSaveException;
import managers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>  {

    @BeforeEach
    public void beforeEach() throws ManagerSaveException {
        File file = new File("src/resources/file.csv");
        taskManager = new FileBackedTasksManager(file);
    }

    @DisplayName("Проверка метода сохранения")
    @Test
    void shouldSaveTasksInFile() throws ManagerSaveException {
        File file = new File("src/resources/file.csv");
        taskManager = new FileBackedTasksManager(file);

        assertTrue(taskManager.getTasks().isEmpty(), "Список содержит таски");
        assertTrue(taskManager.getEpics().isEmpty(), "Список содержит эпики");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список содержит сабтаски");

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 10, Instant.now());
        final int taskId1 = taskManager.createTask(task1);

        Epic epic1 = new Epic("Epic 1", Status.NONE, "Эпик первый", 10, Instant.now(), Instant.now());
        final int epicId1 = taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 1", Status.NONE, "Эпик без подзадач", 10, Instant.now(), Instant.now());
        final int epicId2 = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 10, Instant.now(), epicId1);
        final int subtaskId1 = taskManager.createSubtask(subtask1);

        assertFalse(taskManager.getTasks().isEmpty(), "Список не содержит таски");
        assertFalse(taskManager.getSubtasks().isEmpty(), "Список не содержит сабтаски");
        assertFalse(taskManager.getEpics().isEmpty(), "Список не содержит эпики");

    }

    @DisplayName("Загрузка из файла")
    @Test
    void shouldLoadTasksFromFile() throws ManagerSaveException {
        File file = new File("src/resources/file.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file);

        assertFalse(taskManager.getTasks().isEmpty(), "Список не содержит таски, возможно отсутсвие сохраненного материала");
        assertFalse(taskManager.getEpics().isEmpty(), "Список не содержит эпики, возможно отсутсвие сохраненного материала");
        assertFalse(taskManager.getSubtasks().isEmpty(), "Список не содержит подзадачи, возможно отсутсвие сохраненного материала");

        assertTrue(taskManager.getHistory().isEmpty(), "История не пуста");

        assertEquals(Collections.emptyList(), taskManager.getEpic(3).getSubtaskId(), "Эпик содержит подзадачи");

        assertTrue(taskManager.getEpic(2).getSubtaskId().contains(4), "Эпик не содержит подзадачу");
        assertFalse(taskManager.getHistory().isEmpty(), "История пуста");
    }
}