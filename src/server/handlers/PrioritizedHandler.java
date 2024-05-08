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

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            default:
                sendText(exchange, "Некорректный метод", 400);
        }

    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendText(exchange, response, 200);
    }
}
