package tasks;

import managers.Status;

import java.util.Objects;

public class Subtask extends Task {

    protected int epicId;

    public Subtask(String name, Status status, String description, int epicId) {
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
