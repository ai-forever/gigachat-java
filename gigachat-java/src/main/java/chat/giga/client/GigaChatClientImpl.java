package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.LoggingHttpClient;
import chat.giga.http.client.MediaType;
import chat.giga.http.client.sse.SseListener;
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
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

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
    public static final String CLIENT_ID_HEADER = "X-Client-ID";
    public static final String USER_AGENT = "User-Agent";
    public static final String USER_AGENT_VALUE = "GigaChat-java-lib";

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
            HttpClient authHttpClient,
            Integer readTimeout,
            Integer connectTimeout,
            String apiUrl,
            String authApiUrl,
            boolean logRequests,
            boolean logResponses
    ) {
        this.accessToken = accessToken;
        this.useCertificateAuth = useCertificateAuth;
        this.apiUrl = getOrDefault(apiUrl, DEFAULT_API_URL);
        validateParams(clientId, clientSecret, scope);

        var localHttpClient = apiHttpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : apiHttpClient;

        if (logRequests || logResponses) {
            this.httpClient = new LoggingHttpClient(localHttpClient, logRequests, logResponses);
        } else {
            this.httpClient = localHttpClient;
        }
        this.gigaChatAuthClient = new GigaChatAuthClientImpl(authHttpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : authHttpClient, clientId, clientSecret, scope,
                authApiUrl);
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
                .header(USER_AGENT, USER_AGENT_VALUE)
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
                    .header(USER_AGENT, USER_AGENT_VALUE)
                    .body(objectMapper.writeValueAsBytes(request.toBuilder()
                            .stream(false)
                            .build()))
                    .build();

            var httpResponse = httpClient.execute(httpRequest);

            return objectMapper.readValue(httpResponse.body(), CompletionResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void completions(CompletionRequest request, ResponseHandler<CompletionChunkResponse> handler) {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/chat/completions")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .header(USER_AGENT, USER_AGENT_VALUE)
                    .body(objectMapper.writeValueAsBytes(request.toBuilder()
                            .stream(true)
                            .build()))
                    .build();

            httpClient.execute(httpRequest, new SseListener() {
                @Override
                public void onData(String data) {
                    try {
                        handler.onNext(objectMapper.readValue(data, CompletionChunkResponse.class));
                    } catch (JsonProcessingException e) {
                        handler.onError(e);
                    }
                }

                @Override
                public void onComplete() {
                    handler.onComplete();
                }

                @Override
                public void onError(Throwable th) {
                    handler.onError(th);
                }
            });

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
                    .header(USER_AGENT, USER_AGENT_VALUE)
                    .body(objectMapper.writeValueAsBytes(request))
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
            var requestBody = createMultiPartBody(request.file(), boundary, request.purpose(),
                    request.mimeType(), request.fileName());
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/files")
                    .method(HttpMethod.POST)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=" + boundary)
                    .header(USER_AGENT, USER_AGENT_VALUE)
                    .body(requestBody.toString().getBytes(StandardCharsets.UTF_8))
                    .build();

            var response = httpClient.execute(httpRequest);

            return objectMapper.readValue(response.body(), FileResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public byte[] downloadFile(String fileId, String clientId) {
        var httpRequest = HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId + "/content")
                .method(HttpMethod.GET)
                .header(HttpHeaders.ACCEPT, MediaType.IMAGE_JPG)
                .headerIf(clientId != null, CLIENT_ID_HEADER, clientId)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .header(USER_AGENT, USER_AGENT_VALUE)
                .build();

        var httpResponse = httpClient.execute(httpRequest);
        return httpResponse.body();
    }

    @Override
    public AvailableFilesResponse getListAvailableFile() {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/files")
                    .method(HttpMethod.GET)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(USER_AGENT, USER_AGENT_VALUE)
                    .build();

            var httpResponse = httpClient.execute(httpRequest);

            return objectMapper.readValue(httpResponse.body(), AvailableFilesResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public FileResponse getFileInfo(String fileId) {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/files/" + fileId)
                    .method(HttpMethod.GET)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .header(USER_AGENT, USER_AGENT_VALUE)
                    .build();

            var response = httpClient.execute(httpRequest);

            return objectMapper.readValue(response.body(), FileResponse.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public FileDeletedResponse deleteFile(String fileId) {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/files/" + fileId + "/delete")
                    .method(HttpMethod.POST)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .header(USER_AGENT, USER_AGENT_VALUE)
                    .build();

            var response = httpClient.execute(httpRequest);

            return objectMapper.readValue(response.body(), FileDeletedResponse.class);
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
                    .header(USER_AGENT, USER_AGENT_VALUE)
                    .body(objectMapper.writeValueAsBytes(request))
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
                .header(USER_AGENT, USER_AGENT_VALUE)
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
