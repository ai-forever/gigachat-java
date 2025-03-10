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
            boolean logResponses) {
        super(apiHttpClient, authClient, readTimeout, connectTimeout, apiUrl, logRequests, logResponses);
    }

    @Override
    public ModelResponse models() {
        var httpResponse = httpClient.execute(createModelHttpRequest());

        try {
            return objectMapper.readValue(httpResponse.body(), ModelResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CompletionResponse completions(CompletionRequest request) {
        try {
            var httpResponse = httpClient.execute(createCompletionHttpRequest(request));

            return objectMapper.readValue(httpResponse.body(), CompletionResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public EmbeddingResponse embeddings(EmbeddingRequest request) {
        try {
            var httpResponse = httpClient.execute(createEmbendingHttpRequest(request));

            return objectMapper.readValue(httpResponse.body(), EmbeddingResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public FileResponse uploadFile(UploadFileRequest request) {
        var response = httpClient.execute(createUploadFileHttpRequest(request));

        try {
            return objectMapper.readValue(response.body(), FileResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte[] downloadFile(String fileId, String clientId) {
        return httpClient.execute(createDownloadFileHttpRequest(fileId, clientId))
                .body();
    }

    @Override
    public AvailableFilesResponse getListAvailableFile() {
        var httpResponse = httpClient.execute(createListAvailableFileHttpRequest());

        try {
            return objectMapper.readValue(httpResponse.body(), AvailableFilesResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public FileResponse getFileInfo(String fileId) {
        var response = httpClient.execute(createFileInfoHttpRequest(fileId));

        try {
            return objectMapper.readValue(response.body(), FileResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public FileDeletedResponse deleteFile(String fileId) {
        var response = httpClient.execute(createDeleteFileHttpRequest(fileId));

        try {
            return objectMapper.readValue(response.body(), FileDeletedResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<TokenCount> tokensCount(TokenCountRequest request) {
        try {
            var httpResponse = httpClient.execute(createTokenCountHttpRequest(request));

            return objectMapper.readValue(httpResponse.body(), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public BalanceResponse balance() {
        var httpResponse = httpClient.execute(createBalanceHttpRequest());

        try {
            return objectMapper.readValue(httpResponse.body(), BalanceResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
