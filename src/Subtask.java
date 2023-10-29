import java.util.Objects;

public class Subtask extends Task {

    protected int epicId;

    public Subtask(int id, String name, String status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
    }
    public Subtask(String name, String status, String description, int epicId) {
        super(name, status, description);
        this.epicId = epicId;
    }
    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


}
