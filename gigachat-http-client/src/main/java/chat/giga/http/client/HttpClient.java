package chat.giga.http.client;

import chat.giga.http.client.sse.SseListener;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {

    HttpResponse execute(HttpRequest request);

    void execute(HttpRequest request, SseListener listener);

    CompletableFuture<HttpResponse> executeAsync(HttpRequest request);
}
