package tests;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;


class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private Path path;

    @BeforeEach
    public void setUp() {
        path = Paths.get("test_file.csv");
        manager = new FileBackedTasksManager(path);
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    @AfterEach
    public void clean() {
        manager = null;
        Task.setCount(0);
    }

    @Test
    public void testSaveAndLoadEmptyList() {
        FileBackedTasksManager managerFromFile = FileBackedTasksManager.loadFromFile(path);

        assertEquals(0, managerFromFile.getAllTasks().size());
        assertEquals(0, managerFromFile.getAllEpics().size());
        assertEquals(0, managerFromFile.getAllSubtasks().size());
    }

    @Test
    public void testSaveAndLoadEpicWithoutSubtasks() {
        Epic epic1 = new Epic("Посетить форум", "В ноябре");
        manager.createEpic(epic1);

        FileBackedTasksManager managerFromFile = FileBackedTasksManager.loadFromFile(path);

        assertFalse(managerFromFile.getAllEpics().isEmpty());
        assertTrue(managerFromFile.getEpicById(epic1.getId()).getIdSubtasks().isEmpty());
    }

    @Test
    public void testSaveAndLoadMixedTasks() {
        Task task1 = new Task("Пройти 1ый урок", "Инкапсуляция");
        Task task2 = new Task("Пройти 2ой урок", "Наследование", 90, LocalDateTime.of(2024, 2, 23, 13, 30, 0));
        Epic epic1 = new Epic("Посетить форум", "В ноябре");
        Epic epic2 = new Epic("Посетить форум", "В ноябре");
        Subtask subtask1 = new Subtask("Подготовить доклад", "На 7 минут", epic1.getId());
        Subtask subtask2 = new Subtask("Подготовить презентацию", "На 9 слайдов", epic1.getId(), 80, LocalDateTime.of(2024, 2, 22, 18, 0, 0));
        Subtask subtask3 = new Subtask("Написать проект", "Дипломный", epic1.getId(), 77, LocalDateTime.of(2024, 2, 22, 10, 30, 0));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        FileBackedTasksManager managerFromFile = FileBackedTasksManager.loadFromFile(path);

        assertEquals(2, managerFromFile.getAllTasks().size());
        assertEquals(2, managerFromFile.getAllEpics().size());
        assertEquals(3, managerFromFile.getAllSubtasks().size());
        assertEquals(0, managerFromFile.getHistory().size());
    }


}
