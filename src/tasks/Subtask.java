package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.type = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, int epicId, long duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
        this.type = TypeTask.SUBTASK;
    }

    // for http-test
    public Subtask(String name, String description, LocalDateTime startTime, long duration, int epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        this.type = TypeTask.SUBTASK;
    }

    // for http-test
    public Subtask(Integer id, String name, String description, Status status, LocalDateTime startTime, long duration, int epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    // copy object
    public Subtask(Subtask otherSubtask) {
        super(otherSubtask);
        this.epicId = otherSubtask.epicId;
        this.type = TypeTask.SUBTASK;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return super.toString() + epicId;
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
}
