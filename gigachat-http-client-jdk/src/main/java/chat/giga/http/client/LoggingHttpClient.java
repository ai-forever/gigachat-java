package chat.giga.http.client;

import chat.giga.http.client.sse.SseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static chat.giga.http.client.MediaType.APPLICATION_JSON;

import static java.util.stream.Collectors.joining;

public class LoggingHttpClient implements HttpClient {

    private static final Logger log = LoggerFactory.getLogger(LoggingHttpClient.class);
    private static final Set<String> SECRET_HEADERS = Set.of("authorization");

    private final HttpClient client;
    private final boolean logRequests;
    private final boolean logResponses;

    public LoggingHttpClient(HttpClient client, Boolean logRequests, Boolean logResponses) {
        this.client = client;
        this.logRequests = logRequests;
        this.logResponses = logResponses;
    }

    private void logRequest(HttpRequest httpRequest) {
        try {
            log.debug("""
                            HTTP request:
                            - method: {}
                            - url: {}
                            - headers: {}
                            - body: {}
                            """,
                    httpRequest.method(), httpRequest.url(), format(httpRequest.headers()),
                    isJsonBody(httpRequest.headers()) ? httpRequest.bodyAsString() : "");
        } catch (Exception e) {
            log.error("Exception while logging HTTP request: {}", e.getMessage(), e);
        }
    }

    private boolean isJsonBody(Map<String, List<String>> headers) {
        return headers.entrySet().stream()
                .filter(headerKey -> headerKey.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_TYPE))
                .anyMatch(e -> e.getValue().stream().anyMatch(ee -> ee.contains(APPLICATION_JSON)));
    }

    private void logResponse(String responseDataPart) {
        try {
            log.debug("""
                    HTTP response_part
                    - {}
                    """, responseDataPart);
        } catch (Exception e) {
            log.error("Exception while logging HTTP response: {}", e.getMessage(), e);
        }

    }

    private void logResponse(HttpResponse response) {
        try {
            log.debug("""
                            HTTP response:
                            - status code: {}
                            - headers: {}
                            - body: {}
                            """,
                    response.statusCode(), format(response.headers()),
                    isJsonBody(response.headers()) ? response.bodyAsString() : "");
        } catch (Exception e) {
            log.error("Exception while logging HTTP response: {}", e.getMessage(), e);
        }
    }

    private String format(Map<String, List<String>> headers) {
        return headers.entrySet().stream()
                .filter(e -> !SECRET_HEADERS.contains(e.getKey().toLowerCase()))
                .map(header -> String.format("[%s: %s]", header.getKey(), header.getValue()))
                .collect(joining(", "));
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        if (logRequests) {
            logRequest(request);
        }

        HttpResponse response = client.execute(request);
        if (logResponses) {
            logResponse(response);
        }

        return response;
    }

    @Override
    public void execute(HttpRequest request, SseListener listener) {
        if (logRequests) {
            logRequest(request);
        }

        client.execute(request, new SseListener() {
            @Override
            public void onData(String data) {
                if (logResponses) {
                    logResponse(data);
                }

                listener.onData(data);
            }

            @Override
            public void onComplete() {
                listener.onComplete();
            }

            @Override
            public void onError(Throwable th) {
                listener.onError(th);
            }
        });
    }

    @Override
    public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
        if (logRequests) {
            logRequest(request);
        }

        CompletableFuture<HttpResponse> response = client.executeAsync(request);
        return response.thenApply(e -> {
            if (logResponses) {
                logResponse(e);
            }
            return e;
        });
    }
}
