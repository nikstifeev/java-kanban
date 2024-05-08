package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    protected final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
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
        List<Task> history = manager.getHistory();
        String response = gson.toJson(history);
        sendText(exchange, response, 200);
    }
}
