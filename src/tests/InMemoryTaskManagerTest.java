package tests;

import exceptions.ManagerSaveException;
import managers.HistoryManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager(new EmptyHistoryManager());
    }

    private static class EmptyHistoryManager implements HistoryManager {

        @Override
        public void add(Task task) {

        }

        @Override
        public List<Task> getHistory() {
            return Collections.emptyList();
        }

        @Override
        public void remove(int id) {

        }
    }

    @DisplayName("Расчет статуса эпика")
    @Test
    void shouldCalculateEpicsStatus() throws IOException, ManagerSaveException {

        assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков не пуст");

        Epic epic1 = new Epic("Epic 1", "Эпик первый");
        final int epicId1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("SubTask 1", Status.NEW, "Подзадача эпика первая", 10, LocalDateTime.now(), epicId1);
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика вторая", 10, LocalDateTime.now().plus(40, ChronoUnit.MINUTES), epicId1);

        final int subtaskId1 = taskManager.createSubtask(subtask1);
        final int subtaskId2 = taskManager.createSubtask(subtask2);

        assertTrue(epic1.getStatus().equals(Status.NEW), "Эпик имеет ложный статус (должен быть NEW)");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        taskManager.updateEpic(epic1);
        assertTrue(epic1.getStatus().equals(Status.DONE), "Эпик имеет ложный статус (должен быть DONE)");

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);

        taskManager.updateEpic(epic1);
        assertTrue(epic1.getStatus().equals(Status.IN_PROGRESS), "Эпик имеет ложный статус (должен быть IN_PROGRESS)");

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);

        taskManager.updateEpic(epic1);
        assertTrue(epic1.getStatus().equals(Status.IN_PROGRESS), "Эпик имеет ложный статус (должен быть IN_PROGRESS)");

    }
}
