package tasks;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected String status;
    private static int count = 0;

    public Task(String name, String description) {
        this.id = generateId();
        this.name = name;
        this.description = description;
        this.status = "NEW";
    }

    // copy object
    public Task(Task otherTask) {
        this.id = otherTask.id;
        this.name = otherTask.name;
        this.description = otherTask.description;
        this.status = otherTask.status;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Integer generateId() {
        return ++count;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
