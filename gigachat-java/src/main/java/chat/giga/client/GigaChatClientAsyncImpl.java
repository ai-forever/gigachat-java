package chat.giga.client;

import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.MediaType;
import chat.giga.http.client.sse.SseListener;
import chat.giga.model.BalanceResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.TokenCount;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.completion.CompletionChunkResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.model.file.AvailableFilesResponse;
import chat.giga.model.file.FileDeletedResponse;
import chat.giga.model.file.FileResponse;
import chat.giga.model.file.UploadFileRequest;
import chat.giga.util.RetryUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GigaChatClientAsyncImpl extends BaseGigaChatClient implements GigaChatClientAsync {

    @Builder
    GigaChatClientAsyncImpl(HttpClient apiHttpClient,
            AuthClient authClient,
            Integer readTimeout,
            Integer connectTimeout,
            String apiUrl,
            boolean logRequests,
            boolean logResponses,
            boolean verifySslCerts,
            Integer maxRetriesOnAuthError) {
        super(apiHttpClient, authClient, readTimeout, connectTimeout, apiUrl, logRequests, logResponses, verifySslCerts, maxRetriesOnAuthError);
    }

    @Override
    public CompletableFuture<ModelResponse> models() {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createModelHttpRequest())
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), ModelResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }

    @Override
    public CompletableFuture<CompletionResponse> completions(CompletionRequest request, String sessionId) {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createCompletionHttpRequest(request, sessionId))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), CompletionResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }

    @Override
    public void completions(CompletionRequest request, ResponseHandler<CompletionChunkResponse> handler) {
        try {
            var builder = HttpRequest.builder()
                    .url(apiUrl + "/chat/completions")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM)
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .body(objectMapper.writeValueAsBytes(request.toBuilder()
                            .stream(true)
                            .build()));

            CompletableFuture.runAsync(() -> {
                try {
                    RetryUtils.retry401(() -> {
                        authClient.authenticate(builder);

                        httpClient.execute(builder.build(), new SseListener() {
                            @Override
                            public void onData(String data) {
                                try {
                                    handler.onNext(objectMapper.readValue(data, CompletionChunkResponse.class));
                                } catch (IOException e) {
                                    handler.onError(e);
                                }
                            }

                            @Override
                            public void onComplete() {
                                handler.onComplete();
                            }

                            @Override
                            public void onError(Exception ex) {
                                if (ex instanceof HttpClientException e && e.statusCode() == 401) {
                                    throw e;
                                }
                                handler.onError(ex);
                            }
                        });

                        return null;
                    }, maxRetriesOnAuthError);
                } catch (IllegalStateException e) {
                    handler.onError(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CompletableFuture<EmbeddingResponse> embeddings(EmbeddingRequest request) {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createEmbendingHttpRequest(request))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), EmbeddingResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }

    @Override
    public CompletableFuture<FileResponse> uploadFile(UploadFileRequest request) {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createUploadFileHttpRequest(request))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), FileResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }

    @Override
    public CompletableFuture<ByteArrayInputStream> downloadFile(String fileId, String clientId) {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createDownloadFileHttpRequest(fileId, clientId))
                .thenApply(r -> new ByteArrayInputStream(r.body())), 1);
    }

    @Override
    public CompletableFuture<AvailableFilesResponse> availableFileList() {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createAvailableFileListHttpRequest())
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), AvailableFilesResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }

    @Override
    public CompletableFuture<FileResponse> fileInfo(String fileId) {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createFileInfoHttpRequest(fileId))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), FileResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }

    @Override
    public CompletableFuture<FileDeletedResponse> deleteFile(String fileId) {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createDeleteFileHttpRequest(fileId))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), FileDeletedResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }

    @Override
    public CompletableFuture<List<TokenCount>> tokensCount(TokenCountRequest request) {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createTokenCountHttpRequest(request))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), new TypeReference<>() {
                        });
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }

    @Override
    public CompletableFuture<BalanceResponse> balance() {
        return RetryUtils.retry401Async(() -> httpClient.executeAsync(createBalanceHttpRequest())
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), BalanceResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }), maxRetriesOnAuthError);
    }
}
