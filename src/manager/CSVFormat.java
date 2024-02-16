package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class CSVFormat {

    static Task fromString(String value) {
        String[] parts = value.split(",");
        Task task = null;

        int id = Integer.parseInt(parts[0]);
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (TypeTask.valueOf(parts[1])) {
            case TASK:
                task = new Task(title, description);
                break;
            case EPIC:
                task = new Epic(title, description);
                break;
            case SUBTASK:
                task = new Subtask(title, description, Integer.parseInt(parts[5]));
                break;
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        for (Task task : history) {
            sb.append(task.getId() + ",");
        }
        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        String[] parts = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String item : parts) {
            history.add(Integer.parseInt(item));
        }
        return history;
    }

}
