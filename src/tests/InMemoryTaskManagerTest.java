package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.Task;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }

    @AfterEach
    public void clean() {
        manager = null;
        Task.setCount(0);
    }

}