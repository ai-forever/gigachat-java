package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.MediaType;
import chat.giga.model.BalanceResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.Scope;
import chat.giga.model.TokenCount;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.file.AvailableFilesResponse;
import chat.giga.model.file.UploadFileRequest;
import chat.giga.model.file.FileResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static chat.giga.util.FileUtils.createMultiPartBody;
import static chat.giga.util.Utils.getOrDefault;

import static java.time.Duration.ofSeconds;

class GigaChatClientImpl implements GigaChatClient {

    public static final String DEFAULT_API_URL = "https://gigachat.devices.sberbank.ru/api/v1";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String X_CLIENT_ID = "X-Client-ID";

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
    public FileResponse uploadFile(UploadFileRequest request) {
        String boundary = Long.toHexString(System.currentTimeMillis());
        try {
            var requestBody = createMultiPartBody(request.file(), boundary, request.purpose(), request.mimeType());
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/files")
                    .method(HttpMethod.POST)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=" + boundary)
                    .body(new ByteArrayInputStream(requestBody.toString().getBytes(StandardCharsets.UTF_8)))
                    .build();
            var response = httpClient.execute(httpRequest);
            return objectMapper.readValue(response.body(), FileResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public byte[] downloadFile(String fileId, String xClientId) {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/files/" + fileId + "/content")
                    .method(HttpMethod.GET)
                    .header(HttpHeaders.ACCEPT, MediaType.IMAGE_JPG)
                    .headerIf(xClientId != null, X_CLIENT_ID, xClientId)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .build();
            var httpResponse = httpClient.execute(httpRequest);
            return httpResponse.body().readAllBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public AvailableFilesResponse getListAvailableFile() {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/files")
                    .method(HttpMethod.GET)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .build();
            var httpResponse = httpClient.execute(httpRequest);
            return objectMapper.readValue(httpResponse.body(), AvailableFilesResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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

    @Override
    public BalanceResponse balance() {
        var httpRequest = HttpRequest.builder()
                .url(apiUrl + "/balance")
                .method(HttpMethod.GET)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                .build();

        var httpResponse = httpClient.execute(httpRequest);

        try {
            return objectMapper.readValue(httpResponse.body(), BalanceResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAccessToken() {
        return accessToken == null && !useCertificateAuth ? gigaChatAuthClient.retrieveTokenIfExpired() : accessToken;
    }

    private String buildBearerAuth() {
        return "Bearer " + getAccessToken();
    }
}
