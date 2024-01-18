package tasks;

import managers.TaskType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {

    protected int id;
    protected String name;
    protected Status status;
    protected String description;
    protected int duration;
    protected LocalDateTime startTime;

    public Task(String name, Status status, String description, int duration, LocalDateTime startTime) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {

        return status;
    }

    public void setStatus(Status status) {

        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getEndTime() {
        if(startTime != null) {
            return startTime.plus(duration, ChronoUnit.MINUTES);
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && status == task.status && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }
}


