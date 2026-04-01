package chat.giga.http.client;

import chat.giga.http.client.sse.SseEventListener;
import chat.giga.http.client.sse.SseListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface HttpClient {

    HttpResponse execute(HttpRequest request);

    void execute(HttpRequest request, SseListener listener);

    /**
     * Выполнить запрос и разобрать ответ как SSE с полями {@code event:} / {@code data:} (например API v2).
     * После успешного HTTP-ответа (2xx), до разбора тела, вызывается {@code onSuccessfulStreamResponseHeaders}
     * (если не {@code null}). Обёртка логирования HTTP-клиента передаёт сюда колбэк, чтобы записать заголовки ответа
     * стрима до разбора тела.
     */
    void execute(HttpRequest request, SseEventListener listener,
            BiConsumer<Integer, Map<String, List<String>>> onSuccessfulStreamResponseHeaders);

    /**
     * То же, что {@link #execute(HttpRequest, SseEventListener, BiConsumer)} с {@code null} в качестве колбэка.
     */
    default void execute(HttpRequest request, SseEventListener listener) {
        execute(request, listener, null);
    }

    CompletableFuture<HttpResponse> executeAsync(HttpRequest request);
}
