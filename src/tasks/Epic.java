package tasks;

import managers.Status;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }
}