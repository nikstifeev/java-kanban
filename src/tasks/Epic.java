package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> idSubtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    // copy object
    public Epic(Epic otherEpic) {
        super(otherEpic);
        this.idSubtasks = otherEpic.idSubtasks;
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    public void setIdSubtasks(ArrayList<Integer> idSubtasks) {
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
