package tasks;

import managers.TaskType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Subtask extends Task {

    protected int epicId;


    public Subtask(String name, Status status, String description, int duration, Instant startTime, Instant endTime, int epicId) {
        super(name, status, description, duration, startTime, endTime);
        this.epicId = epicId;
    }

    public Subtask(String name, Status status, String description, int duration, Instant startTime, int epicId) {
        super(name, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", epicId=" + epicId +
                '}';
    }
}
