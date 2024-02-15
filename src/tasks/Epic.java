package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> idSubtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.type = TypeTask.EPIC;
    }

    // copy object
    public Epic(Epic otherEpic) {
        super(otherEpic);
        this.idSubtasks = otherEpic.idSubtasks;
        this.type = TypeTask.EPIC;
    }

    public List<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public void setIdSubtasks(List<Integer> idSubtasks) {
        this.idSubtasks = idSubtasks;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
