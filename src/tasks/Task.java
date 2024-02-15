package tasks;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected TypeTask type;
    private static int count = 0;

    public Task(String name, String description) {
        this.id = generateId();
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = TypeTask.TASK;
    }

    // copy object
    public Task(Task otherTask) {
        this.id = otherTask.id;
        this.name = otherTask.name;
        this.description = otherTask.description;
        this.status = otherTask.status;
        this.type = TypeTask.TASK;
    }

    public int getId() {
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

    public void setId(int id) {
        this.id = id;
    }

    private Integer generateId() {
        return ++count;
    }

    public TypeTask getType() {
        return type;
    }

    public static void setCount(int count) {
        Task.count = count;
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
                description + ",";
    }
}
