package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> idSubtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    // copy object
    public Epic(Epic otherEpic) {
        super(otherEpic);
        this.idSubtasks = otherEpic.idSubtasks;
    }

    public List<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public void setIdSubtasks(List<Integer> idSubtasks) {
        this.idSubtasks = idSubtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", idSubtasks=" + idSubtasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
