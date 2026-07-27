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

    /**
     * Выполнить запрос и вернуть ответ, где тело доступно как InputStream (без загрузки в память). Вызывающая сторона
     * ОБЯЗАНА закрыть InputStream после чтения.
     * <p>
     * Реализация по умолчанию вызывает {@link #execute(HttpRequest)}; поток берётся из {@link HttpResponse#bodyAsStream()},
     * который при отсутствии {@code bodyStream} отдаёт обёртку над {@code body}.
     */
    default HttpResponse executeWithInputStream(HttpRequest request) {
        return execute(request);
    }

    /**
     * Асинхронно выполнить запрос и вернуть ответ, где тело доступно как InputStream (без загрузки в память).
     * Вызывающая сторона ОБЯЗАНА закрыть InputStream после чтения.
     * <p>
     * Реализация по умолчанию вызывает {@link #executeAsync(HttpRequest)}; поток берётся из
     * {@link HttpResponse#bodyAsStream()}.
     */
    default CompletableFuture<HttpResponse> executeAsyncWithInputStream(HttpRequest request) {
        return executeAsync(request);
    }

    /**
     * Закрыть клиент и освободить ресурсы. По умолчанию операция пустая.
     */
    default void close() {
    }

}
