package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls().create();

    public HttpTaskServer(TaskManager manager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
        httpServer.createContext("/epics", new EpicHandler(manager, gson));
        httpServer.createContext("/history", new HistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public static Gson getGson() {
        return gson;
    }

    public void startServer() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    public void stopServer() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту");
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.startServer();
        server.stopServer();
    }
}
