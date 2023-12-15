package managers;

import java.io.File;

public class Managers {

    public static  TaskManager getDefault(File file) {
        return new FileBackedTasksManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
