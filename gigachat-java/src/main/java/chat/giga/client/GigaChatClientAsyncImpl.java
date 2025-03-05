package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.model.BalanceResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.Scope;
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
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class GigaChatClientAsyncImpl extends BaseGigaChatClient implements GigaChatClientAsync {

    @Builder
    GigaChatClientAsyncImpl(String clientId, String clientSecret, Scope scope, String accessToken,
            boolean useCertificateAuth, HttpClient apiHttpClient, HttpClient authHttpClient, Integer readTimeout,
            Integer connectTimeout, String apiUrl, String authApiUrl, boolean logRequests, boolean logResponses) {
        super(clientId, clientSecret, scope, accessToken, useCertificateAuth, apiHttpClient, authHttpClient,
                readTimeout, connectTimeout, apiUrl, authApiUrl, logRequests, logResponses);
    }

    @Override
    public CompletableFuture<ModelResponse> models() {
        return httpClient.executeAsync(createModelHttpRequest())
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), ModelResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<CompletionResponse> completions(CompletionRequest request) {
        try {
            return httpClient.executeAsync(createCompletionHttpRequest(request))
                    .thenApply(r -> {
                        try {
                            return objectMapper.readValue(r.body(), CompletionResponse.class);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CompletableFuture<Void> completions(CompletionRequest request,
            ResponseHandler<CompletionChunkResponse> handler) {
        return CompletableFuture.runAsync(() -> executeCompletionStream(request, handler));
    }

    @Override
    public CompletableFuture<EmbeddingResponse> embeddings(EmbeddingRequest request) {
        try {
            return httpClient.executeAsync(createEmbendingHttpRequest(request))
                    .thenApply(r -> {
                        try {
                            return objectMapper.readValue(r.body(), EmbeddingResponse.class);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CompletableFuture<FileResponse> uploadFile(UploadFileRequest request) {
        return httpClient.executeAsync(createUploadFileHttpRequest(request))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), FileResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<ByteArrayInputStream> downloadFile(String fileId, String clientId) {
        return httpClient.executeAsync(createDownloadFileHttpRequest(fileId, clientId))
                .thenApply(r -> new ByteArrayInputStream(r.body()));
    }

    @Override
    public CompletableFuture<AvailableFilesResponse> getListAvailableFile() {
        return httpClient.executeAsync(createListAvailableFileHttpRequest())
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), AvailableFilesResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<FileResponse> getFileInfo(String fileId) {
        return httpClient.executeAsync(createFileInfoHttpRequest(fileId))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), FileResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<FileDeletedResponse> deleteFile(String fileId) {
        return httpClient.executeAsync(createDeleteFileHttpRequest(fileId))
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), FileDeletedResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<List<TokenCount>> tokensCount(TokenCountRequest request) {
        try {
            return httpClient.executeAsync(createTokenCountHttpRequest(request))
                    .thenApply(r -> {
                        try {
                            return objectMapper.readValue(r.body(), new TypeReference<>() {
                            });
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CompletableFuture<BalanceResponse> balance() {
        return httpClient.executeAsync(createBalanceHttpRequest())
                .thenApply(r -> {
                    try {
                        return objectMapper.readValue(r.body(), BalanceResponse.class);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
