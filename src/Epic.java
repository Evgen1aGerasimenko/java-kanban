import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(int id, String name, String status, String description) {
        super(id, name, status, description);
    }

    public Epic(String name, String status, String description) {
        super(name, status, description);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }
}