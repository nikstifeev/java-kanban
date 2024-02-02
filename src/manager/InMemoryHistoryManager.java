package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodes = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        Node node = nodes.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodes.get(id);
        if (node != null) {
            removeNode(node);
            nodes.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }


    private void linkLast(Task task) {
        Node prevTail = tail;
        Node newNode = new Node(prevTail, task, null);
        tail = newNode;
        if (prevTail == null) {
            head = newNode;
        } else {
            prevTail.next = newNode;
        }
        nodes.put(task.getId(), tail);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = tail;
        while (node != null) {
            tasks.add(node.data);
            node = node.prev;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        Node next = node.next;
        Node prev = node.prev;
        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.data = null;
    }
}
