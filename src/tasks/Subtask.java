package tasks;

import java.time.LocalDateTime;

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

    // copy object
    public Subtask(Subtask otherSubtask) {
        super(otherSubtask);
        this.epicId = otherSubtask.epicId;
        this.type = TypeTask.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public String toString() {
        return super.toString() + epicId;
    }
}
