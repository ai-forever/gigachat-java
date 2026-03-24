package chat.giga.http.client;

import chat.giga.http.client.sse.SseEventListener;
import chat.giga.http.client.sse.SseListener;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {

    HttpResponse execute(HttpRequest request);

    void execute(HttpRequest request, SseListener listener);

    /**
     * Выполнить запрос и разобрать ответ как SSE с полями {@code event:} / {@code data:} (например API v2).
     */
    void execute(HttpRequest request, SseEventListener listener);

    CompletableFuture<HttpResponse> executeAsync(HttpRequest request);
}
