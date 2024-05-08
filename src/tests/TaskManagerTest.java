package tests;

import manager.TaskManager;
import manager.exceptions.NotFoundEpicForSubtaskException;
import manager.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @Test
    public void testGetAllTasks_WithoutTasks() {
        List<Task> allTasks = manager.getAllTasks();

        assertNotNull(allTasks);
        assertTrue(allTasks.isEmpty());
    }

    @Test
    public void testGetAllTasks_WithTasks() {
        Task task1 = new Task("name", "description");
        Task task2 = new Task("name", "description");
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> allTasks = manager.getAllTasks();

        assertNotNull(allTasks);
        assertEquals(2, manager.getAllTasks().size());
        assertTrue(allTasks.contains(task1));
        assertTrue(allTasks.contains(task2));
    }

    @Test
    public void testDeleteAllTasks() {
        Task task1 = new Task("name", "description");
        Task task2 = new Task("name", "description");
        manager.createTask(task1);
        manager.createTask(task2);

        manager.deleteAllTasks();
        List<Task> allTasks = manager.getAllTasks();

        assertTrue(allTasks.isEmpty());
    }

    @Test
    public void testGetTaskById_ExistingId() {
        Task task1 = new Task("name", "description");
        manager.createTask(task1);

        Task task = manager.getTaskById(task1.getId());

        assertNotNull(task);
        assertEquals(task1, task);
    }

    @Test
    public void testGetTaskById_NoExistingId() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            Task task = manager.getTaskById(12);
        });
        assertEquals("Задача с ID: 12 не найдена", exception.getMessage());
    }

    @Test
    public void testCreateTask() {
        Task task1 = new Task("name", "description");
        manager.createTask(task1);

        Task task = manager.getTaskById(task1.getId());
        assertNotNull(task);
        assertEquals(task1, task);
        assertEquals(Status.NEW, task.getStatus());
        assertThrows(NullPointerException.class, () -> manager.createTask(null));
    }

    @Test
    public void testUpdateTask() {
        Task task1 = new Task("name", "description");
        manager.createTask(task1);

        Task updParamTask = new Task(task1);
        updParamTask.setName("newName");
        updParamTask.setDescription("newDecription");
        updParamTask.setStatus(Status.DONE);
        updParamTask.setDuration(80L);
        updParamTask.setStartTime(LocalDateTime.of(2024, 2, 23, 18, 12, 0));

        manager.updateTask(updParamTask);

        Task updTask = manager.getTaskById(task1.getId());
        assertEquals("newName", updTask.getName());
        assertEquals("newDecription", updTask.getDescription());
        assertEquals(Status.DONE, updTask.getStatus());
        assertEquals(80L, updTask.getDuration());
        assertEquals(LocalDateTime.of(2024, 2, 23, 18, 12, 0), updTask.getStartTime());
    }

    @Test
    public void testDeleteTaskById() {
        Task task1 = new Task("name", "description");
        Task task2 = new Task("name", "description");
        manager.createTask(task1);
        manager.createTask(task2);

        manager.deleteTaskById(task1.getId());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            Task deletedTask = manager.getTaskById(task1.getId());
        });
        assertEquals("Задача с ID: 1 не найдена", exception.getMessage());

        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void testGetAllEpics_WithoutEpics() {
        List<Epic> allEpics = manager.getAllEpics();

        assertNotNull(allEpics);
        assertTrue(allEpics.isEmpty());
    }

    @Test
    public void testGetAllEpics_WithEpics() {
        Epic epic1 = new Epic("name", "description");
        Epic epic2 = new Epic("name", "description");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Epic> allEpics = manager.getAllEpics();

        assertNotNull(allEpics);
        assertEquals(2, allEpics.size());
        assertTrue(allEpics.contains(epic1));
        assertTrue(allEpics.contains(epic2));
    }

    @Test
    public void testDeleteAllEpics() {
        Epic epic1 = new Epic("name", "description");
        Epic epic2 = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic1.getId());
        Subtask subtask2 = new Subtask("name", "description", epic2.getId());
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteAllEpics();
        List<Epic> allEpics = manager.getAllEpics();
        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertTrue(allEpics.isEmpty());
        assertTrue(allSubtasks.isEmpty());
    }

    @Test
    public void testGetEpicById_ExistingId() {
        Epic epic1 = new Epic("name", "description");
        manager.createEpic(epic1);

        Epic epic = manager.getEpicById(epic1.getId());

        assertNotNull(epic);
        assertEquals(epic1, epic);
    }

    @Test
    public void testGetEpicById_NoExistingId() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            Epic epic = manager.getEpicById(1);
        });

        assertEquals("Эпик с ID: 1 не найден", exception.getMessage());
    }

    @Test
    public void testCreateEpic() {
        Epic epic1 = new Epic("name", "description");
        manager.createEpic(epic1);

        Epic epic = manager.getEpicById(epic1.getId());
        assertNotNull(epic);
        assertEquals(epic1, epic);
        assertEquals(Status.NEW, epic.getStatus());
        assertThrows(NullPointerException.class, () -> manager.createEpic(null));
    }

    @Test
    public void testUpdateEpic() {
        Epic epic1 = new Epic("name", "description");
        manager.createEpic(epic1);

        Epic updParamEpic = new Epic(epic1);
        updParamEpic.setName("newName");
        updParamEpic.setDescription("newDecription");

        manager.updateEpic(updParamEpic);

        Epic updEpic = manager.getEpicById(epic1.getId());
        assertEquals("newName", updEpic.getName());
        assertEquals("newDecription", updEpic.getDescription());
    }

    @Test
    public void testDeleteEpicById() {
        Epic epic1 = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic1.getId());
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        manager.deleteEpicById(epic1.getId());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            Epic deletedEpic = manager.getEpicById(epic1.getId());
            Subtask deletedSubtask = manager.getSubtaskById(subtask1.getId());
        });

        assertEquals("Эпик с ID: 1 не найден", exception.getMessage());
    }

    @Test
    public void testGetAllSubtasks_WithoutSubtasks() {
        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertNotNull(allSubtasks);
        assertTrue(allSubtasks.isEmpty());
    }

    @Test
    public void testGetAllSubtasks_WithSubtasks() {
        Epic epic1 = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic1.getId());
        Subtask subtask2 = new Subtask("name", "description", epic1.getId());
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertNotNull(allSubtasks);
        assertEquals(2, allSubtasks.size());
        assertTrue(allSubtasks.contains(subtask1));
        assertTrue(allSubtasks.contains(subtask2));
    }

    @Test
    public void testDeleteAllSubtasks() {
        Epic epic1 = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic1.getId());
        Subtask subtask2 = new Subtask("name", "description", epic1.getId());
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteAllSubtasks();
        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertTrue(allSubtasks.isEmpty());
        assertTrue(epic1.getIdSubtasks().isEmpty());
        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void testGetSubtaskById_ExistingId() {
        Epic epic1 = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic1.getId());
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        Subtask subtask = manager.getSubtaskById(subtask1.getId());

        assertNotNull(subtask);
        assertEquals(subtask1, subtask);
    }

    @Test
    public void testGetSubtaskById_NoExistingId() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            Subtask subtask = manager.getSubtaskById(1);
        });
        assertEquals("Подзадача с ID: 1 не найдена", exception.getMessage());
    }

    @Test
    public void testCreateSubtask() {
        Epic epic1 = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic1.getId());
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        Subtask subtask = manager.getSubtaskById(subtask1.getId());
        assertNotNull(subtask);
        assertEquals(subtask1, subtask);
        assertEquals(Status.NEW, subtask1.getStatus());
        assertThrows(NullPointerException.class, () -> manager.createSubtask(null));
    }

    @Test
    public void testCreateSubtaskById_WithoutEpic() {
        Exception exception = assertThrows(NotFoundEpicForSubtaskException.class, () -> {
            Subtask subtask1 = new Subtask("name", "description", 152);
            manager.createSubtask(subtask1);
            Subtask subtask = manager.getSubtaskById(subtask1.getId());
        });

        assertEquals("Эпик с ID: 152 для подзадачи не найден.", exception.getMessage());
    }

    @Test
    public void testUpdateSubtask() {
        Epic epic1 = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic1.getId());
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        Subtask updParamSubtask = new Subtask(subtask1);
        updParamSubtask.setName("newName");
        updParamSubtask.setDescription("newDecription");
        updParamSubtask.setStatus(Status.IN_PROGRESS);
        updParamSubtask.setDuration(80L);
        updParamSubtask.setStartTime(LocalDateTime.of(2024, 2, 23, 18, 12, 0));

        manager.updateSubtask(updParamSubtask);

        Subtask updSubtask = manager.getSubtaskById(subtask1.getId());
        assertEquals("newName", updSubtask.getName());
        assertEquals("newDecription", updSubtask.getDescription());
        assertEquals(Status.IN_PROGRESS, updSubtask.getStatus());
        assertEquals(80L, updSubtask.getDuration());
        assertEquals(LocalDateTime.of(2024, 2, 23, 18, 12, 0), updSubtask.getStartTime());
    }

    @Test
    public void testDeleteSubtaskById() {
        Epic epic1 = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic1.getId());
        Subtask subtask2 = new Subtask("name", "description", epic1.getId());
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteSubtaskById(subtask1.getId());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            Subtask deletedSubtask = manager.getSubtaskById(subtask1.getId());
        });

        assertEquals("Подзадача с ID: 2 не найдена", exception.getMessage());
        assertEquals(1, epic1.getIdSubtasks().size());
    }

    @Test
    public void testGetAllSubtasksByEpic() {
        Epic epic = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic.getId());
        Subtask subtask2 = new Subtask("name", "description", epic.getId());
        Subtask subtask3 = new Subtask("name", "description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        List<Subtask> subtasksByEpic = manager.getAllSubtasksByEpic(epic);

        assertEquals(3, subtasksByEpic.size());
        assertEquals(subtask1, subtasksByEpic.get(0));
        assertEquals(subtask2, subtasksByEpic.get(1));
        assertTrue(subtasksByEpic.contains(subtask3));
    }

    @Test
    public void testGetAllSubtasksByEmptyEpic() {
        Epic epic = new Epic("name", "description");
        manager.createEpic(epic);

        List<Subtask> subtasksByEpic = manager.getAllSubtasksByEpic(epic);

        assertTrue(subtasksByEpic.isEmpty());
    }

    @Test
    public void testGetHistoryEmptyHistory() {
        List<Task> history = manager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    public void testGetHistoryWithTasks() {
        Task task = new Task("name", "description");
        Epic epic = new Epic("name", "description");
        Subtask subtask = new Subtask("name", "description", epic.getId());
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        List<Task> history = manager.getHistory();

        assertEquals(3, history.size());
        assertEquals(task, history.get(2));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(0));
    }

    @Test
    public void testGetPrioritizedTasks() {
        Task task = new Task("name", "description", 80, LocalDateTime.of(2024, 2, 23, 15, 30, 0));
        Epic epic = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic.getId(), 60, LocalDateTime.of(2024, 2, 23, 18, 15, 0));
        Subtask subtask2 = new Subtask("name", "description", epic.getId(), 60, LocalDateTime.of(2024, 2, 22, 18, 15, 0));
        Subtask subtask3 = new Subtask("name", "description", epic.getId());
        Subtask subtask4 = new Subtask("name", "description", epic.getId(), 60, LocalDateTime.of(2024, 2, 23, 10, 0, 0));
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.createSubtask(subtask4);

        Set<Task> expectedPrioritizedTasks = new TreeSet<>((t1, t2) -> {
            if (t1.getStartTime() == null && t2.getStartTime() == null) {
                return Integer.compare(t1.getId(), t2.getId());
            } else if (t1.getStartTime() == null) {
                return 1;
            } else if (t2.getStartTime() == null) {
                return -1;
            } else {
                return t1.getStartTime().compareTo(t2.getStartTime());
            }
        });
        expectedPrioritizedTasks.add(task);
        expectedPrioritizedTasks.add(subtask1);
        expectedPrioritizedTasks.add(subtask2);
        expectedPrioritizedTasks.add(subtask3);
        expectedPrioritizedTasks.add(subtask4);

        Set<Task> prioritizedTasks = manager.getPrioritizedTasks();

        Task[] arrayFromExpected = expectedPrioritizedTasks.toArray(new Task[0]);
        Task[] arrayFromActualSet = prioritizedTasks.toArray(new Task[0]);

        assertArrayEquals(arrayFromExpected, arrayFromActualSet);
    }

    @Test
    public void testUpdateEpicStatus_New_NoSubtasks() {
        Epic epic = new Epic("name", "description");
        manager.createEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatus_New_AllSubtasksNew() {
        Epic epic = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic.getId());
        Subtask subtask2 = new Subtask("name", "description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatus_InProgress_MixedStatus() {
        Epic epic = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic.getId());
        Subtask subtask2 = new Subtask("name", "description", epic.getId());
        Subtask subtask3 = new Subtask("name", "description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        Subtask updParamSubtask1 = new Subtask(subtask1);
        updParamSubtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(updParamSubtask1);

        Subtask updParamSubtask2 = new Subtask(subtask2);
        updParamSubtask2.setStatus(Status.DONE);
        manager.updateSubtask(updParamSubtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatus_InProgress_SubtasksWithoutInProgress() {
        Epic epic = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic.getId());
        Subtask subtask2 = new Subtask("name", "description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Subtask updParamSubtask1 = new Subtask(subtask1);
        updParamSubtask1.setStatus(Status.DONE);
        manager.updateSubtask(updParamSubtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testUpdateEpicStatus_Done_AllSubtasksDone() {
        Epic epic = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic.getId());
        Subtask subtask2 = new Subtask("name", "description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Subtask updParamSubtask1 = new Subtask(subtask1);
        updParamSubtask1.setStatus(Status.DONE);
        manager.updateSubtask(updParamSubtask1);

        Subtask updParamSubtask2 = new Subtask(subtask2);
        updParamSubtask2.setStatus(Status.DONE);
        manager.updateSubtask(updParamSubtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testUpdateEpicTime_WithoutSubtasks() {
        Epic epic = new Epic("name", "description");
        manager.createEpic(epic);

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertNull(epic.getDuration());
    }

    @Test
    public void testUpdateEpicTime_SubtasksWithoutDuration() {
        Epic epic = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic.getId());
        Subtask subtask2 = new Subtask("name", "description", epic.getId());
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(0, epic.getDuration());
    }

    @Test
    public void testUpdateEpicTime_AllSubtasksWithDuration() {
        Epic epic = new Epic("name", "description");
        Subtask subtask1 = new Subtask("name", "description", epic.getId(), 55, LocalDateTime.of(2024, 2, 23, 10, 0, 0));
        Subtask subtask2 = new Subtask("name", "description", epic.getId(), 22, LocalDateTime.of(2024, 2, 23, 16, 0, 0));
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        LocalDateTime earliestStartTime = subtask1.getStartTime();
        LocalDateTime latestEndTime = subtask2.getEndTime();
        long totalDuration = subtask1.getDuration() + subtask2.getDuration();

        assertEquals(earliestStartTime, epic.getStartTime());
        assertEquals(latestEndTime, epic.getEndTime());
        assertEquals(totalDuration, epic.getDuration());
    }

}