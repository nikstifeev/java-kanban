package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(parts[2]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

}
