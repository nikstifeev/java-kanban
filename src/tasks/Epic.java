package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> idSubtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.type = TypeTask.EPIC;
    }

    public Epic(String name, String description, int id) {
        super(id, name, description);
    }


    // copy object
    public Epic(Epic otherEpic) {
        super(otherEpic);
        this.idSubtasks = otherEpic.idSubtasks;
        this.type = TypeTask.EPIC;
        this.endTime = otherEpic.endTime;
    }

    public List<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setIdSubtasks(List<Integer> idSubtasks) {
        this.idSubtasks = idSubtasks;
    }

    @Override
    public String toString() {
        return super.toString() + idSubtasks + ", " + endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(idSubtasks, epic.idSubtasks) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idSubtasks, endTime);
    }
}
