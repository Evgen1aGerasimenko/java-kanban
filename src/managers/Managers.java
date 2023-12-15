package managers;

import java.io.File;

public class Managers {

    public static  TaskManager getDefault() {
        return new FileBackedTasksManager(new File("src/resources/file.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
