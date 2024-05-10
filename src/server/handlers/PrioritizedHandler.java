package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {
    protected final TaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    protected void handleGet(HttpExchange exchange, String path) throws IOException {
        Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendText(exchange, response, 200);
    }

    protected void handlePost(HttpExchange exchange, String path) throws IOException {
        sendText(exchange, "Некорректный метод", 400);
    }

    protected void handleDelete(HttpExchange exchange, String path) throws IOException {
        sendText(exchange, "Некорректный метод", 400);
    }
}
