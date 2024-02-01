package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exceptions.ManagerSaveException;
import managers.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    protected TaskManager taskManager;
    protected HttpServer server;
    public static final int PORT = 8080;
    protected final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = getGson();
        server = HttpServer.create();
        server.bind(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
    }

    private void handler(HttpExchange h) throws IOException{
        try {
            System.out.println("\n/tasks: " + h.getRequestURI());
            final String path = h.getRequestURI().getPath().substring("/load/".length());

            switch (path) {
                case "/task":
                    handleTask(h);
                    break;
                case "/subtask":
                    handleSubtask(h);
                    break;
                case "/epic":
                    handleEpic(h);
                    break;
                case "/subtask/epic":
                   handleEpicsSubtasks(h);
                    break;
                case "/prioritized":
                    handlePrioritized(h);
                    break;
                case "/":
                    handleAllTasks(h);
                    break;

                case "/history":
                    handleHistory(h);
                    break;
                default:
                    System.out.println("Неизвестный запрос: " + h.getRequestURI());
                    h.sendResponseHeaders(404, 0);
            }
        }  catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        h.close();
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(),  StandardCharsets.UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    protected static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    private void handleEpicsSubtasks(HttpExchange h) throws IOException {
        if (!h.getRequestMethod().equals("GET")) {
            System.out.println("/subtask/epic ждет GET-запроос, а получил: " + h.getRequestMethod());
            h.sendResponseHeaders(405, 0);
        }

        final String query = h.getRequestURI().getQuery();
        final int id = Integer.parseInt(query);
        final List<Subtask> subtasks = taskManager.getSubtasksOfTasks(id);
        final String response = gson.toJson(subtasks);
        System.out.println("Получены подзадачи эпика с идентификатором " + id);
        sendText(h, response);
    }

    private void handlePrioritized(HttpExchange h) throws IOException {
        if (!h.getRequestMethod().equals("GET")) {
            h.sendResponseHeaders(405, 0);
        }
        final String responsePrioritized = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(h, responsePrioritized);
    }

    private void handleAllTasks(HttpExchange h) throws IOException {
        if (!h.getRequestMethod().equals("GET")) {
            h.sendResponseHeaders(405, 0);
        }
        final String responseAllTasks = gson.toJson(taskManager.getTasks());
        final String responseAllSubtasks = gson.toJson(taskManager.getSubtasks());
        final String responseAllEpics = gson.toJson(taskManager.getEpics());

        sendText(h, responseAllTasks);
        sendText(h, responseAllSubtasks);
        sendText(h, responseAllEpics);
    }

    private void handleHistory(HttpExchange h) throws IOException {
        if (!h.getRequestMethod().equals("GET")) {
            h.sendResponseHeaders(405, 0);
        }
        final String responseHistory = gson.toJson(taskManager.getHistory());
        sendText(h, responseHistory);
    }

    private void handleTask(HttpExchange h) throws IOException, ManagerSaveException {

        final String query = h.getRequestURI().getQuery();

        switch (h.getRequestMethod()) {
            case "GET":
                if (query == null) {
                    final List<Task> tasks = taskManager.getTasks();
                    final String response = gson.toJson(tasks);
                    System.out.println("Все задачи получены");
                    sendText(h, response);
                    return;
                }
                final int id = Integer.parseInt(query);
                final Task task = taskManager.getTask(id);
                final String response = gson.toJson(task);
                System.out.println("Получена задача id = " + id);
                sendText(h, response);
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.clearAllTasks();
                    System.out.println("Все задачи удалены");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                final int id1 = Integer.parseInt(query);
                taskManager.deleteTask(id1);
                System.out.println("Удалена задача id = " + id1);
                h.sendResponseHeaders(200, 0);
                break;
            case "POST":
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("В теле запроса указывается body с пустой задачей");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Task task1 = gson.fromJson(json, Task.class);
                final Integer id2 = task1.getId();
                if(taskManager.getTasks().contains(taskManager.getTask(id2))) {
                    taskManager.updateTask(task1);
                    System.out.println("Обновлена задача id = " + id2);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.createTask(task1);
                    final Integer newId = task1.getId();
                    System.out.println("Создана задача id = " + newId);
                    h.sendResponseHeaders(200, 0);
                }
                break;
            default:
                System.out.println("disabled");

        }
    }

    private void handleEpic(HttpExchange h) throws IOException, ManagerSaveException {

        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                if (query == null) {
                    final List<Epic> epics = taskManager.getEpics();
                    final String response = gson.toJson(epics);
                    System.out.println("Все эпики получены");
                    sendText(h, response);
                    return;
                }
                final int id = Integer.parseInt(query);
                final Epic epic = taskManager.getEpic(id);
                final String response = gson.toJson(epic);
                System.out.println("Получен эпик id = " + id);
                sendText(h, response);
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.clearAllEpics();
                    System.out.println("Все эпики удалены");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                final int id1 = Integer.parseInt(query);
                taskManager.deleteEpic(id1);
                System.out.println("Удален эпик id = " + id1);
                h.sendResponseHeaders(200, 0);
                break;
            case "POST":
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("В теле запроса указывается body с пустым эпиком");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Epic epic1 = gson.fromJson(json, Epic.class);
                final Integer id2 = epic1.getId();
                if(taskManager.getEpics().contains(taskManager.getEpic(id2))) {
                    taskManager.updateEpic(epic1);
                    final Integer newId = epic1.getId();
                    System.out.println("Обновлен epic id = " + newId);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.createEpic(epic1);
                    final Integer newId = epic1.getId();
                    System.out.println("Создан epic id = " + newId);
                    h.sendResponseHeaders(200, 0);
                }
                break;
            default:
                System.out.println("disabled");

        }
    }

    private void handleSubtask(HttpExchange h) throws IOException, ManagerSaveException {

        final String query = h.getRequestURI().getQuery();

        switch (h.getRequestMethod()) {
            case "GET":
                if (query == null) {
                    final List<Subtask> subtasks = taskManager.getSubtasks();
                    final String response = gson.toJson(subtasks);
                    System.out.println("Все подзадачи получены");
                    sendText(h, response);
                    return;
                }
                final int id = Integer.parseInt(query);
                final Subtask subtask = taskManager.getSubtask(id);
                final String response = gson.toJson(subtask);
                System.out.println("Получена подзадача id = " + id);
                sendText(h, response);
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.clearAllSubtasks();
                    System.out.println("Все задачи удалены");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                final int id1 = Integer.parseInt(query);
                taskManager.deleteSubtask(id1);
                System.out.println("Удалена подзадача id = " + id1);
                h.sendResponseHeaders(200, 0);
                break;
            case "POST":
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("В теле запроса указывается body с пустой подзадачей");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Subtask subtask1 = gson.fromJson(json, Subtask.class);
                final Integer id2 = subtask1.getId();
                if(taskManager.getSubtasks().contains(taskManager.getSubtask(id2))) {
                    taskManager.updateSubtask(subtask1);
                    System.out.println("Обновлен subtask id = " + id2);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.createSubtask(subtask1);
                    final Integer newId = subtask1.getId();
                    System.out.println("Создан subtask id = " + newId);
                    h.sendResponseHeaders(200, 0);
                }
                break;
            default:
                System.out.println("disabled");

        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }
    public void stop() {
        server.stop(1);
        System.out.println("Bye httpTaskServer");
    }
}
