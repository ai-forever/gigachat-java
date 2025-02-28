package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.model.DownloadFileRequest;
import chat.giga.model.DownloadFileResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.Scope;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.TokenCountResponse;
import chat.giga.model.UploadFileRequest;
import chat.giga.model.UploadFileResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static chat.giga.util.Utils.getOrDefault;

import static java.time.Duration.ofSeconds;

class GigaChatClientImpl implements GigaChatClient {

    private static final String DEFAULT_API_URL = "https://gigachat.devices.sberbank.ru/api/v1";

    private final GigaChatAuthClient gigaChatAuthClient;
    private final HttpClient httpClient;
    private final String accessToken;
    private final boolean useCertificateAuth;
    private final String apiUrl;
    private final ObjectMapper objectMapper = JsonUtils.objectMapper();


    @Builder
    GigaChatClientImpl(String clientId,
            String clientSecret,
            Scope scope,
            String accessToken,
            boolean useCertificateAuth,
            HttpClient apiHttpClient,
            HttpClient authHtpClient,
            Integer readTimeout,
            Integer connectTimeout,
            String apiUrl,
            String authApiUrl
    ) {
        this.accessToken = accessToken;
        this.useCertificateAuth = useCertificateAuth;
        this.apiUrl = apiUrl;
        this.httpClient = apiHttpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : apiHttpClient;

        this.gigaChatAuthClient = new GigaChatAuthClientImpl(authHtpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : authHtpClient, clientId, clientSecret, scope,
                authApiUrl);
    }

    private String getApiUrl() {
        return getOrDefault(this.apiUrl, DEFAULT_API_URL);
    }

    private String getAccessToken() {
        return accessToken == null && !useCertificateAuth ? gigaChatAuthClient.retrieveTokenIfExpired() : accessToken;
    }

    @Override
    public ModelResponse models() {
        var httpRequest = HttpRequest.builder()
                .url(getApiUrl() + "/models")
                .method(HttpMethod.GET)
                .headerIf(!useCertificateAuth, "Authorization", "Bearer " + getAccessToken())
                .header("Accept", "application/json")
                .header("X-Request-ID", UUID.randomUUID().toString())
                .build();


        var httpResponse = httpClient.execute(httpRequest);

        try {
            return objectMapper.readValue(httpResponse.body(), ModelResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletionResponse completions(CompletionRequest request) {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(getApiUrl() + "/chat/completions")
                    .method(HttpMethod.POST)
                    .headerIf(!useCertificateAuth, "Authorization", "Bearer " + getAccessToken())
                    .header("Accept", "application/json")
                    .header("X-Request-ID", UUID.randomUUID().toString())
                    .body(new ByteArrayInputStream(objectMapper.writeValueAsBytes(request)))
                    .build();

            var httpResponse = httpClient.execute(httpRequest);

            return objectMapper.readValue(httpResponse.body(), CompletionResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmbeddingResponse embeddings(EmbeddingRequest request) {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(getApiUrl() + "/embeddings")
                    .method(HttpMethod.POST)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .headerIf(!useCertificateAuth, "Authorization", "Bearer " + getAccessToken())
                    .header("X-Request-ID", UUID.randomUUID().toString())
                    .body(new ByteArrayInputStream(objectMapper.writeValueAsBytes(request)))
                    .build();

            var httpResponse = httpClient.execute(httpRequest);
            return objectMapper.readValue(httpResponse.body(), EmbeddingResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) {
        return null;
    }

    @Override
    public DownloadFileResponse downloadFile(DownloadFileRequest request) {
        return null;
    }

    @Override
    public TokenCountResponse tokensCount(TokenCountRequest request) {
        return null;
    }
}
