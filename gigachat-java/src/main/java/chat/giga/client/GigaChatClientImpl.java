package chat.giga.client;

import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import chat.giga.model.BalanceResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.TokenCount;
import chat.giga.model.TokenCountRequest;
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class GigaChatClientImpl extends BaseGigaChatClient implements GigaChatClient {

    @Builder
    GigaChatClientImpl(HttpClient apiHttpClient,
            AuthClient authClient,
            Integer readTimeout,
            Integer connectTimeout,
            String apiUrl,
            boolean logRequests,
            boolean logResponses,
            boolean verifySslCerts) {
        super(apiHttpClient, authClient, readTimeout, connectTimeout, apiUrl, logRequests, logResponses, verifySslCerts);
    }

    @Override
    public ModelResponse models() {
        var httpResponse = RetryUtils.retry401(() -> httpClient.execute(createModelHttpRequest()), MAX_RETRIES);

        try {
            return objectMapper.readValue(httpResponse.body(), ModelResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CompletionResponse completions(CompletionRequest request, String sessionId) {
        try {
            var httpResponse = RetryUtils.retry401(() -> httpClient.execute(createCompletionHttpRequest(request, sessionId)),
                    MAX_RETRIES);

            return objectMapper.readValue(httpResponse.body(), CompletionResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public EmbeddingResponse embeddings(EmbeddingRequest request) {
        try {
            var httpResponse = RetryUtils.retry401(() -> httpClient.execute(createEmbendingHttpRequest(request)),
                    MAX_RETRIES);

            return objectMapper.readValue(httpResponse.body(), EmbeddingResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public FileResponse uploadFile(UploadFileRequest request) {
        var response = RetryUtils.retry401(() -> httpClient.execute(createUploadFileHttpRequest(request)), MAX_RETRIES);

        try {
            return objectMapper.readValue(response.body(), FileResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte[] downloadFile(String fileId, String clientId) {
        return RetryUtils.retry401(() -> httpClient.execute(createDownloadFileHttpRequest(fileId, clientId)),
                        MAX_RETRIES)
                .body();
    }

    @Override
    public AvailableFilesResponse getListAvailableFile() {
        var httpResponse = RetryUtils.retry401(() -> httpClient.execute(createListAvailableFileHttpRequest()),
                MAX_RETRIES);

        try {
            return objectMapper.readValue(httpResponse.body(), AvailableFilesResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public FileResponse getFileInfo(String fileId) {
        var response = RetryUtils.retry401(() -> httpClient.execute(createFileInfoHttpRequest(fileId)), MAX_RETRIES);

        try {
            return objectMapper.readValue(response.body(), FileResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public FileDeletedResponse deleteFile(String fileId) {
        var response = RetryUtils.retry401(() -> httpClient.execute(createDeleteFileHttpRequest(fileId)), MAX_RETRIES);

        try {
            return objectMapper.readValue(response.body(), FileDeletedResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<TokenCount> tokensCount(TokenCountRequest request) {
        var httpResponse = RetryUtils.retry401(() -> httpClient.execute(createTokenCountHttpRequest(request)),
                MAX_RETRIES);

        try {
            return objectMapper.readValue(httpResponse.body(), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public BalanceResponse balance() {
        var httpResponse = RetryUtils.retry401(() -> httpClient.execute(createBalanceHttpRequest()), MAX_RETRIES);

        try {
            return objectMapper.readValue(httpResponse.body(), BalanceResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
