package managers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import exceptions.ManagerSaveException;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    protected KVTaskClient client;
    protected final Gson gson = Managers.getGson();
    public HttpTaskManager(int port, boolean load) throws IOException, ManagerSaveException {
        super(null);
        this.client = new KVTaskClient(port);
        if(load) {
            load();
        }
    }
    public HttpTaskManager(int port) throws IOException, ManagerSaveException {
        this(port, false);
    }


    protected void addTasks(List<? extends Task> tasks) {
        for(Task task : tasks) {
            final int id = task.getId();
            TaskType type = task.getType();
            if(type == TaskType.TASK) {
                this.tasks.put(id, task);
                prioritizedTasks.add(task);
            } else if (type == TaskType.SUBTASK) {
                subtasks.put(id,(Subtask) task);
                prioritizedTasks.add(task);
            } else if (type == TaskType.EPIC) {
                epics.put(id, (Epic) task);
            }
        }
    }



    private void load() throws IOException, ManagerSaveException {

        JsonElement jsonTasks = JsonParser.parseString(client.load("tasks"));
        JsonElement jsonEpics = JsonParser.parseString(client.load("epics"));
        JsonElement jsonSubtasks = JsonParser.parseString(client.load("subtasks"));
        JsonElement jsonHistory = JsonParser.parseString(client.load("history"));

            List<Task> tasks = gson.fromJson(jsonTasks, new TypeToken<ArrayList<Task>>() {}.getType());
            addTasks(tasks);
            List<Epic> epics = gson.fromJson(jsonEpics, new TypeToken<ArrayList<Epic>>() {}.getType());
            addTasks(epics);
            List<Subtask> subtasks = gson.fromJson(jsonSubtasks, new TypeToken<ArrayList<Subtask>>() {}.getType());
            addTasks(subtasks);
            List<Integer> history = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Integer>>() {}.getType());

        for (Integer historyId : history) {
            for (var x : tasks) {
                if (this.tasks.containsKey(historyId)) {
                    getTask(historyId);
                }
            }
            for (var x : subtasks) {
                if (this.subtasks.containsKey(historyId)) {
                    getSubtask(historyId);
                }
            }
            for (var x : epics) {
                if (this.epics.containsKey(historyId)) {
                    getEpic(historyId);
                }
            }

        }
    }


    @Override
    protected void save() {

        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);

        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks", jsonSubtasks);

        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics", jsonEpics);

        String jsonHistory = gson.toJson(historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }
}
