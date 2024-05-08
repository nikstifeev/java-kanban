package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.exceptions.NotFoundException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TypeTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    protected final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) {
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
        Optional<Integer> epicId = getId(exchange);

        if (parts.length == 2) {
            List<Epic> epics = manager.getAllEpics();
            String response = gson.toJson(epics);
            sendText(exchange, response, 200);
        } else if (parts.length == 3) {
            if (epicId.isEmpty()) {
                sendText(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }
            try {
                Epic epic = manager.getEpicById(epicId.get());
                String response = gson.toJson(epic);
                sendText(exchange, response, 200);
            } catch (NotFoundException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else if (parts.length == 4 && parts[3].equals("subtasks")) {
            if (epicId.isEmpty()) {
                sendText(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }
            try {
                Epic epic = manager.getEpicById(epicId.get());
                List<Subtask> subtasksByEpic = manager.getAllSubtasksByEpic(epic);
                String response = gson.toJson(subtasksByEpic);
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
        Epic epic = gson.fromJson(body, Epic.class);
        epic.setType(TypeTask.EPIC);
        if (parts.length == 2) {
            try {
                if (epic.getId() == null) {
                    epic.setId(Task.generateId());
                    manager.createEpic(epic);
                    sendText(exchange, "Эпик создан", 201);
                } else if (manager.getEpicById(epic.getId()) != null) {
                    manager.updateEpic(epic);
                    sendText(exchange, "Эпик обновлен", 201);
                }
            } catch (NotFoundException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else {
            sendText(exchange, "Некорректный URL", 400);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        Optional<Integer> epicId = getId(exchange);
        if (parts.length == 3) {
            if (epicId.isEmpty()) {
                sendText(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }
            try {
                Epic epic = manager.getEpicById(epicId.get());
                manager.deleteEpicById(epic.getId());
                sendText(exchange, "Эпик удален", 200);
            } catch (NotFoundException e) {
                sendText(exchange, e.getMessage(), 404);
            }
        } else {
            sendText(exchange, "Некорректный URL", 400);
        }
    }

}
