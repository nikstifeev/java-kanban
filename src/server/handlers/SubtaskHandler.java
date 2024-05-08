package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.NotFoundEpicForSubtaskException;
import manager.exceptions.NotFoundException;
import manager.TaskManager;
import manager.exceptions.TaskTimeOverlapException;
import tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    protected final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGet(exchange, path);
                break;
            case "POST":
                handlePost(exchange, path);
                break;
            case "DELETE":
                handleDelete(exchange, path);
                break;
            default:
                sendText(exchange, "Некорректный метод", 400);
        }

    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        Optional<Integer> subtaskId = getId(exchange);
        if (parts.length == 2) {
            List<Subtask> subtasks = manager.getAllSubtasks();
            String response = gson.toJson(subtasks);
            sendText(exchange, response, 200);
        } else if (parts.length == 3) {
            if (subtaskId.isEmpty()) {
                sendText(exchange, "Некорректный идентификатор подзадачи", 400);
                return;
            }
            try {
                Subtask subtask = manager.getSubtaskById(subtaskId.get());
                String response = gson.toJson(subtask);
                sendText(exchange, response, 200);
            } catch (NotFoundException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else {
            sendText(exchange, "Некорректный URL", 400);
        }
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        subtask.setType(TypeTask.SUBTASK);
        if (parts.length == 2) {
            try {
                if (subtask.getId() == null) {
                    subtask.setId(Task.generateId());
                    subtask.setStatus(Status.NEW);
                    manager.createSubtask(subtask);
                    sendText(exchange, "Подзадача создана", 201);
                } else if (manager.getSubtaskById(subtask.getId()) != null) {
                    manager.updateSubtask(subtask);
                    sendText(exchange, "Подзадача обновлена", 201);
                }
            } catch (TaskTimeOverlapException e) {
                sendText(exchange, e.getMessage(), 406);
            } catch (NotFoundException | NotFoundEpicForSubtaskException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else {
            sendText(exchange, "Некорректный URL", 400);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        Optional<Integer> subtaskId = getId(exchange);
        if (parts.length == 3) {
            if (subtaskId.isEmpty()) {
                sendText(exchange, "Некорректный идентификатор подзадачи", 400);
                return;
            }
            try {
                Subtask subtask = manager.getSubtaskById(subtaskId.get());
                manager.deleteSubtaskById(subtask.getId());
                sendText(exchange, "Подзадача удалена", 200);
            } catch (NotFoundException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else {
            sendText(exchange, "Некорректный URL", 400);
        }
    }


}
