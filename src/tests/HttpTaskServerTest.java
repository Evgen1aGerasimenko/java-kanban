package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exceptions.ManagerSaveException;
import managers.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    private Task task;
    private Subtask subtask;
    private Epic epic;
    Gson gson = getGson();
    private static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    @BeforeEach
    public void beforeEach() throws IOException, ManagerSaveException {

        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(taskManager);

        task = new Task("Task 1", Status.NEW, "Таск номер один", 1, LocalDateTime.now().plusMinutes(10));
        taskManager.createTask(task);
        epic = new Epic("Epic 1", "Эпик первый");
        final int epicId = taskManager.createEpic(epic);
        subtask = new Subtask("SubTask 1", Status.DONE, "Подзадача эпика первая", 1, LocalDateTime.now(), epicId);
        taskManager.createSubtask(subtask);

        taskServer.start();
    }

    @AfterEach
    public void afterEach() {
        taskServer.stop();
    }

    @DisplayName("Возвращаем список задач")
    @Test
    void shouldReturnTaskList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());

        assertNotNull(tasks, "Список задач пуст");
        assertEquals(1,tasks.size(), "Количество задач не соответсвует ожидамому");
        Task actualTask = tasks.get(0);
        assertEquals(task, actualTask, "Задачи не совпадают");
        assertEquals(task.getId(), actualTask.getId(), "Идентификаторы задач не совпадают");
        assertEquals(task.getName(), actualTask.getName(), "Названия задач не совпадают");
        assertEquals(task.getStartTime(), actualTask.getStartTime(), "Время начала задач не совпадает");
        assertEquals(task.getEndTime(), actualTask.getEndTime(), "Время окончания задач не совпадает");
        assertEquals(task.getDuration(), actualTask.getDuration(), "Продолжительность задач не совпадает");
        assertEquals(task.getDescription(), actualTask.getDescription(), "Описание задач не совпадает");
        assertEquals(task.getStatus(), actualTask.getStatus(), "Статусы задач не совпадают");

    }

    @DisplayName("Возвращаем задачу по идентификатору")
    @Test
    void shouldReturnTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskReturned = gson.fromJson(response.body(), Task.class);

        assertNotNull(taskReturned, "Пустая задача");
        assertEquals(task, taskReturned, "Задачи не совпадают");

    }

    @DisplayName("Удаляем весь список задач")
    @Test
    void shouldClearTasksList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {

        }.getType());
        assertNull(tasks, "Список задач не пуст");
    }

    @DisplayName("Удаляем задачу по идентификатору")
    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {

        }.getType());
        assertNull(tasks, "Список задач не пуст");
    }

    @DisplayName("Добавляем новую задачу")
    @Test
    void shouldAddTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Task task2 = new Task("Task 2", "Таск номер 2");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getTasks().isEmpty(),"Список пуст");
        assertEquals(2,taskManager.getTasks().size(), "Список должен содержать две задачи");

    }
    @DisplayName("Заменяем существующую задачу")
    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        Task task2 = new Task("Task 2", "Таск номер 2");
        task2.setId(task.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getTasks().isEmpty(),"Список пуст");
        assertEquals(1,taskManager.getTasks().size(), "Список должен содержать одну задачу");
        assertEquals(task,task2, "Задача не обновлена");

    }

    @DisplayName("Возвращаем список подзадач")
    @Test
    void shouldReturnSubtaskList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {}.getType());

        assertNotNull(subtasks, "Список подзадач пуст");
        System.out.println(subtasks);
        assertEquals(1, subtasks.size(), "Количество подзадач не соответсвует ожидамому");
        Subtask actualSubtask = subtasks.get(0);
        assertEquals(subtask, actualSubtask, "Подзадачи не совпадают");
        assertEquals(subtask.getId(), actualSubtask.getId(), "Идентификаторы подзадач не совпадают");
        assertEquals(subtask.getName(), actualSubtask.getName(), "Названия подзадач не совпадают");
        assertEquals(subtask.getStartTime(), actualSubtask.getStartTime(), "Время начала подзадач не совпадает");
        assertEquals(subtask.getEndTime(), actualSubtask.getEndTime(), "Время окончания подзадач не совпадает");
        assertEquals(subtask.getDuration(), actualSubtask.getDuration(), "Продолжительность подзадач не совпадает");
        assertEquals(subtask.getDescription(), actualSubtask.getDescription(), "Описание подзадач не совпадает");
        assertEquals(subtask.getStatus(), actualSubtask.getStatus(), "Статусы подзадач не совпадают");

    }

    @DisplayName("Возвращаем подзадачу по идентификатору")
    @Test
    void shouldReturnSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask?3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtaskReturned = gson.fromJson(response.body(), Subtask.class);

        assertNotNull(subtaskReturned, "Пустая подзадача");
        assertEquals(subtask, subtaskReturned, "Подзадачи не совпадают");

    }

    @DisplayName("Удаляем весь список подзадач")
    @Test
    void shouldClearSubtasksList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {

        }.getType());
        assertNull(subtasks, "Список подзадач не пуст");
    }

    @DisplayName("Удаляем подзадачу по идентификатору")
    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask?3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {

        }.getType());
        assertNull(subtasks, "Список подзадач не пуст");
    }

    @DisplayName("Добавляем новую подзадачу")
    @Test
    void shouldAddSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика 2nd", 1, LocalDateTime.now(), epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getSubtasks().isEmpty(),"Список пуст");
        assertEquals(2,taskManager.getSubtasks().size(), "Список должен содержать две подзадачи");

    }
    @DisplayName("Заменяем существующую подзадачу")
    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        Subtask subtask2 = new Subtask("SubTask 2", Status.NEW, "Подзадача эпика 2nd", 1, LocalDateTime.now(), epic.getId());
        subtask2.setId(subtask.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getSubtasks().isEmpty(),"Список пуст");
        assertEquals(1,taskManager.getSubtasks().size(), "Список должен содержать одну подзадачу");
        assertEquals(subtask,subtask2, "Подзадача не обновлена");

    }

    @DisplayName("Возвращаем список эпиков")
    @Test
    void shouldReturnEpicsList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {

        }.getType());

        assertNotNull(epics, "Список эпиков пуст");
        assertEquals(1, epics.size(), "Количество эпиков не соответсвует ожидамому");
        Epic actualEpic = epics.get(0);
        assertEquals(epic, actualEpic, "Эпики не совпадают");
        assertEquals(epic.getId(), actualEpic.getId(), "Идентификаторы эпиков не совпадают");
        assertEquals(epic.getName(), actualEpic.getName(), "Названия эпиков не совпадают");
        assertEquals(epic.getStartTime(), actualEpic.getStartTime(), "Время начала эпиков не совпадает");
        assertEquals(epic.getEndTime(), actualEpic.getEndTime(), "Время окончания эпиков не совпадает");
        assertEquals(epic.getDuration(), actualEpic.getDuration(), "Продолжительность эпиков не совпадает");
        assertEquals(epic.getDescription(), actualEpic.getDescription(), "Описание эпиков не совпадает");
        assertEquals(epic.getStatus(), actualEpic.getStatus(), "Статусы эпиков не совпадают");

    }

    @DisplayName("Возвращаем эпик по идентификатору")
    @Test
    void shouldReturnEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic?2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic epicReturned = gson.fromJson(response.body(), Epic.class);

        assertNotNull(epicReturned, "Пустая задача");
        assertEquals(epic, epicReturned, "Задачи не совпадают");

    }

    @DisplayName("Удаляем весь список эпиков")
    @Test
    void shouldClearEpicsList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {

        }.getType());
        assertNull(epics, "Список эпиков не пуст");
    }

    @DisplayName("Удаляем эпик по идентификатору")
    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic?2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {

        }.getType());
        assertNull(epics, "Список эпиков не пуст");
    }

    @DisplayName("Добавляем новый эпик")
    @Test
    void shouldAddEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        Epic epic2 = new Epic("Epic 2", "Epic номер 2");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getEpics().isEmpty(),"Список пуст");
        assertEquals(2,taskManager.getEpics().size(), "Список должен содержать два эпика");


    }
    @DisplayName("Заменяем существующий эпик")
    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        Epic epic2 = new Epic("Epic 2", "Epic номер 2");
        epic2.setId(epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getEpics().isEmpty(),"Список пуст");
        assertEquals(1,taskManager.getEpics().size(), "Список должен содержать один эпик");
        assertEquals(epic.getId(),epic2.getId(), "Эпик не обновлен");
        assertEquals(epic.getName(),epic2.getName(), "Эпик не обновлен");
        assertEquals(epic.getDescription(),epic2.getDescription(), "Эпик не обновлен");

    }

    @DisplayName("Возвращаем список приоритетных задач")
    @Test
    void shouldReturnListOfPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Task> priritizedTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {

        }.getType());
        assertNotNull(priritizedTasks, "Список пуст");
        assertEquals(2, priritizedTasks.size(), "Количество задач не соответсвует ожидаемому");

    }

    @DisplayName("Получаем все задачи")
    @Test
    void shouldReturnAllTasks() throws IOException, InterruptedException, ManagerSaveException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Task> allTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());

        final List<Task> allSubtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {}.getType());

        final List<Task> allEpics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {}.getType());

        assertNotNull(allTasks, "Список пуст");
        assertEquals(1, allTasks.size(), "Количество задач не соответсвует ожидаемому");
        assertNotNull(allEpics, "Список пуст");
        assertEquals(1, allEpics.size(), "Количество эпиков не соответсвует ожидаемому");
        assertNotNull(allSubtasks, "Список пуст");
        assertEquals(1, allSubtasks.size(), "Количество подзадач не соответсвует ожидаемому");

    }

    @DisplayName("Получаем историю")
    @Test
    void shouldReturnHistory() throws IOException, InterruptedException, ManagerSaveException {
        taskManager.getEpic(epic.getId());
        HttpClient client = HttpClient.newHttpClient();
        taskManager.getEpic(epic.getId());
        taskManager.getTask(task.getId());

        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        final List<Task> history = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());
        assertNotNull(history, "Список пуст");
        assertEquals(2, history.size(), "Количество задач не соответсвует ожидаемому");

    }

    @DisplayName("Получаем подзадачи эпика")
    @Test
    void shouldReturnEpicsSubtasks() throws IOException, InterruptedException, ManagerSaveException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic?2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Subtask> epicsSubtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>(){}.getType());
        assertNotNull(epicsSubtasks, "Список пуст");
        assertEquals(1, epicsSubtasks.size(), "Количество подзадач не соответсвует ожидаемому");

    }
}
