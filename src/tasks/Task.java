package tasks;

import com.google.gson.annotations.Expose;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;
    protected TypeTask type;
    protected Long duration;
    protected LocalDateTime startTime;
    private static int count = 0;

    public Task(String name, String description) {
        this.id = generateId();
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = TypeTask.TASK;
    }

    public Task(Integer id, String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, long duration, LocalDateTime startTime) {
        this.id = generateId();
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = TypeTask.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    // for http-test
    public Task(String name, String description, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    // for http-test
    public Task(Integer id, String name, String description, Status status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TypeTask.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    // copy object
    public Task(Task otherTask) {
        this.id = otherTask.id;
        this.name = otherTask.name;
        this.description = otherTask.description;
        this.status = otherTask.status;
        this.type = TypeTask.TASK;
        this.duration = otherTask.duration;
        this.startTime = otherTask.startTime;
    }

    public void setType(TypeTask type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static Integer generateId() {
        return ++count;
    }

    public TypeTask getType() {
        return type;
    }

    public static void setCount(int count) {
        Task.count = count;
    }

    public static int getCount() {
        return count;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plusMinutes(duration);
        }
        return null;
    }

    @Override
    public int compareTo(Task otherTask) {
        if (this.getStartTime() == null && otherTask.getStartTime() == null) {
            return Integer.compare(this.getId(), otherTask.getId());
        } else if (this.getStartTime() == null) {
            return 1;
        } else if (otherTask.getStartTime() == null) {
            return -1;
        } else {
            return this.getStartTime().compareTo(otherTask.getStartTime());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && type == task.type && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, type, duration, startTime);
    }

    @Override
    public String toString() {
        TypeTask type = null;
        switch (getClass().getSimpleName()) {
            case "Task":
                type = TypeTask.TASK;
                break;
            case "Epic":
                type = TypeTask.EPIC;
                break;
            case "Subtask":
                type = TypeTask.SUBTASK;
                break;
        }
        return id + "," +
                type + "," +
                name + "," +
                status + "," +
                description + "," +
                duration + "," +
                startTime + ",";
    }
}
