package tasks;

import managers.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    protected ArrayList<Integer> subtaskId = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String name, Status status, String description, int duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(name, status, description, duration, startTime);
        this.endTime = endTime;

    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
    }


    @Override
    public String toString() {
        return "Epic{" +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}