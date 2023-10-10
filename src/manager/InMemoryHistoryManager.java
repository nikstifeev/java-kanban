package manager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.pollLast();
        }
        history.addFirst(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}
