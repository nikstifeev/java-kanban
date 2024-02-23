package tests;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends InMemoryHistoryManager {

    HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @AfterEach
    public void clean() {
        historyManager = null;
    }

    @Test
    public void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    public void testAddToHistory() {
        Task task1 = new Task("name", "description");
        Task task2 = new Task("name", "description");
        Task task3 = new Task("name", "description");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(task3, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task1, history.get(2));
    }

    @Test
    void testDuplicateTask() {
        Task task1 = new Task("name", "description");
        Task task2 = new Task("name", "description");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
    }

    @Test
    void testRemoveFromBegin() {
        Task task1 = new Task("name", "description");
        Task task2 = new Task("name", "description");
        Task task3 = new Task("name", "description");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task3, history.get(0));
        assertEquals(task2, history.get(1));
        assertFalse(history.contains(task1));
    }

    @Test
    void testRemoveFromMid() {
        Task task1 = new Task("name", "description");
        Task task2 = new Task("name", "description");
        Task task3 = new Task("name", "description");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task3, history.get(0));
        assertEquals(task1, history.get(1));
        assertFalse(history.contains(task2));
    }

    @Test
    void testRemoveFromEnd() {
        Task task1 = new Task("name", "description");
        Task task2 = new Task("name", "description");
        Task task3 = new Task("name", "description");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
        assertFalse(history.contains(task3));
    }

}