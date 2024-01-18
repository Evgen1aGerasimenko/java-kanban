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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

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

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 10, LocalDateTime.now());
        final int taskId1 = taskManager.createTask(task1);

        Epic epic1 = new Epic("Epic 1", "Эпик первый");
        final int epicId1 = taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 1","Эпик без подзадач");
        final int epicId2 = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 10, LocalDateTime.now().plus(20, ChronoUnit.MINUTES), epicId1);
        final int subtaskId1 = taskManager.createSubtask(subtask1);

        assertFalse(taskManager.getTasks().isEmpty(), "Список не содержит таски");
        assertFalse(taskManager.getSubtasks().isEmpty(), "Список не содержит сабтаски");
        assertFalse(taskManager.getEpics().isEmpty(), "Список не содержит эпики");

        assertEquals(Collections.emptyList(), epic2.getSubtaskId(), "Список эпиков вероятно содержит подзадачи");
        assertTrue(taskManager.getHistory().isEmpty(), "Список истории не пуст");


    }

    @DisplayName("Загрузка из файла")
    @Test
    void shouldLoadTasksFromFile() throws ManagerSaveException {

        File file = new File("src/resources/file.csv");
        taskManager = new FileBackedTasksManager(file);

        Task task1 = new Task("Task 1", Status.NEW, "Таск номер один", 10, LocalDateTime.now());
        final int taskId1 = taskManager.createTask(task1);

        Epic epic1 = new Epic("Epic 1", "Эпик первый");
        final int epicId1 = taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 1", "Эпик без подзадач");
        final int epicId2 = taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 10, LocalDateTime.now().plus(20, ChronoUnit.MINUTES), epicId1);
        final int subtaskId1 = taskManager.createSubtask(subtask1);

        TaskManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(taskManager.getTasks(), fileBackedTasksManager.getTasks(), "Список не содержит таски, возможно отсутствие сохраненного материала");
        assertEquals(taskManager.getEpics(), fileBackedTasksManager.getEpics(), "Список не содержит эпики, возможно отсутствие сохраненного материала");
        assertEquals(taskManager.getSubtasks(), fileBackedTasksManager.getSubtasks(), "Список не содержит подзадачи, возможно отсутствие сохраненного материала");

        assertTrue(taskManager.getHistory().isEmpty(), "История не пуста");
        taskManager.getTask(epicId1);
        assertFalse(fileBackedTasksManager.getEpics().isEmpty(), "Список истории пуст");
        assertTrue(fileBackedTasksManager.getEpic(epicId2).getSubtaskId().isEmpty(), "Эпик содержит подзадачи");
        assertTrue(fileBackedTasksManager.getEpic(epicId1).getSubtaskId().contains(subtaskId1), "Эпик не содержит подзадач");

    }
}