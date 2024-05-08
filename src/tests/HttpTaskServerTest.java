package tests;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import manager.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws Exception {
        Task.setCount(0);
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        taskServer.startServer();
    }

    @AfterEach
    public void cleanUp() throws Exception {
        taskServer.stopServer();
    }

    @Test
    public void testPOSTtask_create() throws IOException, InterruptedException {
        Task task = new Task("новый таск", "тест таска", LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskJson)).uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("новый таск", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testPOSTtask_create_timeOverlap() throws IOException, InterruptedException {
        Task task = new Task("новый таск", "тест таска", LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        Task task2 = new Task("новый таск", "тест таска", LocalDateTime.of(2024, 5, 17, 14, 0, 0), 60);
        String taskJson = gson.toJson(task);
        String task2Json = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskJson)).uri(url).build();
        HttpRequest request2 = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(task2Json)).uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode());
    }

    @Test
    public void testGETtasks() throws IOException, InterruptedException {
        Task task = new Task(1, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGETtaskById() throws IOException, InterruptedException {
        Task task = new Task(1, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGETtaskById_NonExistent() throws IOException, InterruptedException {
        Task task = new Task(1, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/tasks/1fs");
        HttpRequest request2 = HttpRequest.newBuilder().GET().uri(url2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response2.statusCode());
    }

    @Test
    public void testPOSTtask_update() throws IOException, InterruptedException {
        Task task = new Task(1, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        manager.createTask(task);

        Task updatedTask = new Task(1, "обновленный таск", "тест таска", Status.IN_PROGRESS, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals("обновленный таск", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals(Status.IN_PROGRESS, tasksFromManager.get(0).getStatus(), "Некорректный статус");
    }

    @Test
    public void testDELETEtask() throws IOException, InterruptedException {
        Task task = new Task(1, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        Task task2 = new Task(2, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 15, 30, 0), 60);
        manager.createTask(task);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        Exception exception = assertThrows(NotFoundException.class, () -> {
            manager.getTaskById(task.getId());
        });
        assertEquals("Задача с ID: 1 не найдена", exception.getMessage());
    }

    @Test
    public void testDELETEepic() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(0, epicsFromManager.size(), "Некорректное количество задач");

        Exception exception = assertThrows(NotFoundException.class, () -> {
            manager.getEpicById(epic.getId());
        });
        assertEquals("Эпик с ID: 1 не найден", exception.getMessage());
    }

    @Test
    public void testDELETEsubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "новый сабтаск", "тест сабтаска", Status.NEW, LocalDateTime.of(2024, 5, 17, 14, 10, 0), 60, 1);
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertEquals(0, subtasksFromManager.size(), "Некорректное количество задач");

        Exception exception = assertThrows(NotFoundException.class, () -> {
            manager.getSubtaskById(subtask.getId());
        });
        assertEquals("Подзадача с ID: 2 не найдена", exception.getMessage());
    }

    @Test
    public void testGETepics() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testPOSTepic_create() throws IOException, InterruptedException {
        String epicJson = "{\"name\":\"новый эпик\",\"description\":\"тест эпика\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testPOSTsubtask_create() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("новый сабтаск", "тест сабтаска", LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60, 1);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("новый сабтаск", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testPOSTsubtask_create_timeOverlap() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("новый сабтаск", "тест сабтаска", LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60, 1);
        String subtaskJson = gson.toJson(subtask);
        Subtask subtask2 = new Subtask("новый сабтаск", "тест сабтаска", LocalDateTime.of(2024, 5, 17, 14, 10, 0), 60, 1);
        String subtaskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).uri(url).build();
        HttpRequest request2 = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(subtaskJson2)).uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode());
    }

    @Test
    public void testGETsubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "новый сабтаск", "тест сабтаска", Status.NEW, LocalDateTime.of(2024, 5, 17, 14, 10, 0), 60, 1);
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
    }

    @Test
    public void testGETsubtaskById_NonExistent() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60, 1);
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/27");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/tasks/1fs");
        HttpRequest request2 = HttpRequest.newBuilder().GET().uri(url2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response2.statusCode());
    }

    @Test
    public void testGETepicById() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.get(0).getId(), "Некорректная задача");
    }

    @Test
    public void testGETepicById_NonExistent() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/27");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/epics/1fs");
        HttpRequest request2 = HttpRequest.newBuilder().GET().uri(url2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response2.statusCode());
    }

    @Test
    public void testGETSubtasksByEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60, 1);
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask(3, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 15, 30, 0), 60, 1);
        manager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(2, subtasks.size(), "Неправильное кол-во задач");

        URI url2 = URI.create("http://localhost:8080/epics/17/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().GET().uri(url2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response2.statusCode());
    }

    @Test
    public void testGETsubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60, 1);
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testPOSTsubtask_update() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60, 1);
        manager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask(2, "обновленный таск", "тест таска", Status.DONE, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60, 1);
        String updatedSubtaskJson = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        assertEquals("обновленный таск", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals(Status.DONE, subtasksFromManager.get(0).getStatus(), "Некорректный статус");
    }

    @Test
    public void testGEThistory() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2025, 5, 17, 13, 30, 0), 60, 1);
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask(3, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2026, 5, 17, 13, 30, 0), 60, 1);
        manager.createSubtask(subtask2);
        Task task = new Task(4, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        manager.createTask(task);

        manager.getTaskById(task.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> history = manager.getHistory();
        assertNotNull(history, "Задачи не возвращаются");
        assertEquals(4, history.size(), "Некорректное количество задач");
        assertEquals(2, history.get(0).getId(), "Неправильная расположение задачи в истории");
        assertEquals(1, history.get(1).getId(), "Неправильная расположение задачи в истории");
        assertEquals(3, history.get(2).getId(), "Неправильная расположение задачи в истории");
        assertEquals(4, history.get(3).getId(), "Неправильная расположение задачи в истории");
    }

    @Test
    public void testGETprioritized() throws IOException, InterruptedException {
        Epic epic = new Epic("новый эпик", "тест эпика");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2025, 5, 17, 13, 30, 0), 60, 1);
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask(3, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2026, 5, 17, 13, 30, 0), 60, 1);
        manager.createSubtask(subtask2);
        Task task = new Task(4, "новый таск", "тест таска", Status.NEW, LocalDateTime.of(2024, 5, 17, 13, 30, 0), 60);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
        List<Task> tasks = new ArrayList<Task>(prioritizedTasks);
        assertNotNull(prioritizedTasks, "Задачи не возвращаются");
        assertEquals(3, prioritizedTasks.size(), "Некорректное количество задач");
        assertEquals(4, tasks.get(0).getId(), "Некорректное расположение задачи");
        assertEquals(2, tasks.get(1).getId(), "Некорректное расположение задачи");
        assertEquals(3, tasks.get(2).getId(), "Некорректное расположение задачи");

    }
    


}
