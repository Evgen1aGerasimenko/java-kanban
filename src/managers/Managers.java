package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.ManagerSaveException;
import server.KVServer;

import java.io.File;
import java.io.IOException;

public class Managers {

    public static  TaskManager getDefault() throws IOException, ManagerSaveException {
        return new HttpTaskManager(KVServer.PORT);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }
    public static  TaskManager getDefaultFileBackedManager(File file) {
        return new FileBackedTasksManager(file);
    }
}
