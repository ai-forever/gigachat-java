package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.MediaType;
import chat.giga.model.DownloadFileRequest;
import chat.giga.model.DownloadFileResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.Scope;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.UploadFileRequest;
import chat.giga.model.UploadFileResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.model.token.TokenCount;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static chat.giga.util.Utils.getOrDefault;

import static java.time.Duration.ofSeconds;

class GigaChatClientImpl implements GigaChatClient {

    public static final String DEFAULT_API_URL = "https://gigachat.devices.sberbank.ru/api/v1";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";

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
        this.apiUrl = getOrDefault(apiUrl, DEFAULT_API_URL);
        this.httpClient = apiHttpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : apiHttpClient;

        this.gigaChatAuthClient = new GigaChatAuthClientImpl(authHtpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : authHtpClient, clientId, clientSecret, scope,
                authApiUrl);
        validateParams(clientId, clientSecret, scope);
    }

    private void validateParams(String clientId, String clientSecret, Scope scope) {
        if (!useCertificateAuth && accessToken == null) {
            Objects.requireNonNull(clientId, "clientId must not be null");
            Objects.requireNonNull(clientSecret, "clientSecret must not be null");
            Objects.requireNonNull(scope, "scope must not be null");

        }
    }

    @Override
    public ModelResponse models() {
        var httpRequest = HttpRequest.builder()
                .url(apiUrl + "/models")
                .method(HttpMethod.GET)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
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
                    .url(apiUrl + "/chat/completions")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
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
                    .url(apiUrl + "/embeddings")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
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
    public List<TokenCount> tokensCount(TokenCountRequest request) {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/tokens/count")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .body(new ByteArrayInputStream(objectMapper.writeValueAsBytes(request)))
                    .build();

            var httpResponse = httpClient.execute(httpRequest);
            return objectMapper.readValue(httpResponse.body(), new TypeReference<>() {
            });
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getAccessToken() {
        return accessToken == null && !useCertificateAuth ? gigaChatAuthClient.retrieveTokenIfExpired() : accessToken;
    }

    private String buildBearerAuth() {
        return "Bearer " + getAccessToken();
    }
}
