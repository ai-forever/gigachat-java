package chat.giga.http.client;

import java.util.concurrent.CompletableFuture;

public interface HttpClient {

    HttpResponse execute(HttpRequest request);

    CompletableFuture<HttpResponse> executeAsync(HttpRequest request);
}
