package chat.giga.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class JdkHttpClient implements HttpClient {

    private final java.net.http.HttpClient delegate;
    private final Duration readTimeout;

    public JdkHttpClient(JdkHttpClientBuilder builder) {
        var httpClientBuilder = builder.httpClientBuilder();

        if (builder.connectTimeout() != null) {
            httpClientBuilder.connectTimeout(builder.connectTimeout());
        }
        this.delegate = httpClientBuilder.build();
        this.readTimeout = builder.readTimeout();
    }

    public static JdkHttpClientBuilder builder() {
        return new JdkHttpClientBuilder();
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        try {
            var jdkResponse = delegate.send(mapJdkRequest(request), BodyHandlers.ofInputStream());

            if (!isSuccessful(jdkResponse)) {
                throw new HttpClientException(jdkResponse.statusCode(), jdkResponse.body());
            }

            return mapResponse(jdkResponse);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
        var jdkRequest = mapJdkRequest(request);

        return delegate.sendAsync(jdkRequest, BodyHandlers.ofInputStream())
                .thenApply(r -> {
                    if (!isSuccessful(r)) {
                        throw new HttpClientException(r.statusCode(), r.body());
                    }
                    return mapResponse(r);
                });
    }

    private java.net.http.HttpRequest mapJdkRequest(HttpRequest request) {
        var builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(request.url()));

        request.headers().forEach((name, values) -> {
            if (values != null) {
                values.forEach(value -> builder.header(name, value));
            }
        });

        BodyPublisher bodyPublisher;
        if (request.body() != null) {
            bodyPublisher = BodyPublishers.ofInputStream(request::body);
        } else {
            bodyPublisher = BodyPublishers.noBody();
        }
        builder.method(request.method().name(), bodyPublisher);

        if (readTimeout != null) {
            builder.timeout(readTimeout);
        }

        return builder.build();
    }

    private static boolean isSuccessful(java.net.http.HttpResponse<InputStream> jdkResponse) {
        var statusCode = jdkResponse.statusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    private static HttpResponse mapResponse(java.net.http.HttpResponse<InputStream> jdkResponse) {
        return HttpResponse.builder()
                .statusCode(jdkResponse.statusCode())
                .headers(jdkResponse.headers().map())
                .body(jdkResponse.body())
                .build();
    }
}
