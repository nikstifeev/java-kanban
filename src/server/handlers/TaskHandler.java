package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.NotFoundException;
import manager.TaskManager;
import manager.exceptions.TaskTimeOverlapException;
import tasks.Status;
import tasks.Task;
import tasks.TypeTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    protected final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        Optional<Integer> taskId = getId(exchange);
        if (parts.length == 2) {
            List<Task> tasks = manager.getAllTasks();
            String response = gson.toJson(tasks);
            sendText(exchange, response, 200);
        } else if (parts.length == 3) {
            if (taskId.isEmpty()) {
                sendText(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }
            try {
                Task task = manager.getTaskById(taskId.get());
                String response = gson.toJson(task);
                sendText(exchange, response, 200);
            } catch (NotFoundException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else {
            sendText(exchange, "Некорректный URL", 400);
        }
    }

    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        task.setType(TypeTask.TASK);
        if (parts.length == 2) {
            try {
                if (task.getId() == null) {
                    task.setId(Task.generateId());
                    task.setStatus(Status.NEW);
                    manager.createTask(task);
                    sendText(exchange, "Задача создана", 201);
                } else if (manager.getTaskById(task.getId()) != null) {
                    manager.updateTask(task);
                    sendText(exchange, "Задача обновлена", 201);
                }
            } catch (TaskTimeOverlapException e) {
                sendText(exchange, e.getMessage(), 406);
            } catch (NotFoundException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else {
            sendText(exchange, "Некорректный URL", 400);
        }
    }

    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        Optional<Integer> taskId = getId(exchange);
        if (parts.length == 3) {
            if (taskId.isEmpty()) {
                sendText(exchange, "Некорректный идентификатор задачи", 400);
                return;
            }
            try {
                Task task = manager.getTaskById(taskId.get());
                manager.deleteTaskById(task.getId());
                sendText(exchange, "Задача удалена", 200);
            } catch (NotFoundException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else {
            sendText(exchange, "Некорректный URL", 400);
        }
    }
}
