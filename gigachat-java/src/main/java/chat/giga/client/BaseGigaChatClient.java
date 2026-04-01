package chat.giga.client;

import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.LoggingHttpClient;
import chat.giga.http.client.MediaType;
import chat.giga.http.client.SSL;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.batch.BatchMethod;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.file.UploadFileRequest;
import chat.giga.model.filter.FilterCheckRequest;
import chat.giga.model.v2.completion.CompletionRequestV2;
import chat.giga.util.FileUtils;
import chat.giga.util.JsonUtils;
import chat.giga.util.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.UUID;

import static java.time.Duration.ofSeconds;

abstract class BaseGigaChatClient {

    public static final String DEFAULT_API_URL = "https://gigachat.devices.sberbank.ru/api/v1";
    public static final String DEFAULT_API_V2_URL = "https://gigachat.devices.sberbank.ru/v2";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String CLIENT_ID_HEADER = "X-Client-ID";
    public static final String SESSION_ID_HEADER = "X-Session-ID";
    public static final String USER_AGENT_NAME = "GigaChat-java-lib";
    public static final int MAX_RETRIES = 1;

    protected final int maxRetriesOnAuthError;
    protected final AuthClient authClient;
    protected final HttpClient httpClient;
    protected final String apiUrl;
    protected final String apiV2Url;
    protected final ObjectMapper objectMapper = JsonUtils.objectMapper();

    protected BaseGigaChatClient(HttpClient apiHttpClient,
            AuthClient authClient,
            Integer readTimeout,
            Integer connectTimeout,
            String apiUrl,
            String apiV2Url,
            boolean logRequests,
            boolean logResponses,
            Boolean verifySslCerts,
            Integer maxRetriesOnAuthError) {
        Objects.requireNonNull(authClient, "authClient must not be null");
        this.apiUrl = Utils.getOrDefault(apiUrl, DEFAULT_API_URL);
        this.apiV2Url = apiV2Url != null ? apiV2Url : deriveApiV2Url(this.apiUrl);
        this.authClient = authClient;
        this.maxRetriesOnAuthError = Utils.getOrDefault(maxRetriesOnAuthError, MAX_RETRIES);

        var client = authClient.supportsHttpClient() ? authClient.getHttpClient()
                : (apiHttpClient == null ? new JdkHttpClientBuilder()
                        .readTimeout(ofSeconds(Utils.getOrDefault(readTimeout, 15)))
                        .connectTimeout(ofSeconds(Utils.getOrDefault(connectTimeout, 15)))
                        .ssl(mapSslConfig(Utils.getOrDefault(verifySslCerts, true)))
                        .build() : apiHttpClient);

        if (logRequests || logResponses) {
            this.httpClient = new LoggingHttpClient(client, logRequests, logResponses);
        } else {
            this.httpClient = client;
        }
    }

    protected HttpRequest createModelHttpRequest() {
        var builder = HttpRequest.builder()
                .url(apiUrl + "/models")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString());

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createCompletionHttpRequest(CompletionRequest request, String sessionId) {
        HttpRequestBuilder builder;
        try {
            builder = HttpRequest.builder()
                    .url(apiUrl + "/chat/completions")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .headerIf(sessionId != null, SESSION_ID_HEADER, sessionId)
                    .body(objectMapper.writeValueAsBytes(request.toBuilder()
                            .stream(false)
                            .build()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        authClient.authenticate(builder);

        return builder.build();
    }

    /**
     * База URL для API v2. Если {@code apiUrl} оканчивается на {@code /api/v1}, заменяется на {@code /api/v2}; иначе
     * используется {@link #DEFAULT_API_V2_URL}.
     */
    public static String deriveApiV2Url(String apiUrl) {
        if (apiUrl == null || apiUrl.isEmpty()) {
            return DEFAULT_API_V2_URL;
        }
        String suffix = "/api/v1";
        if (apiUrl.endsWith(suffix)) {
            return apiUrl.substring(0, apiUrl.length() - suffix.length()) + "/v2";
        }
        return DEFAULT_API_V2_URL;
    }

    protected HttpRequest createCompletionV2HttpRequest(CompletionRequestV2 request, String sessionId) {
        CompletionRequestV2 forHttp = request;
        if (Boolean.TRUE.equals(request.stream())) {
            forHttp = request.toBuilder().stream(false).build();
        }
        HttpRequestBuilder builder;
        try {
            builder = HttpRequest.builder()
                    .url(apiV2Url + "/chat/completions")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .headerIf(sessionId != null, SESSION_ID_HEADER, sessionId)
                    .body(objectMapper.writeValueAsBytes(forHttp));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        authClient.authenticate(builder);

        return builder.build();
    }

    /**
     * Подготовка запроса потоковых completions v2 (SSE). Не вызывает {@link AuthClient#authenticate}. В теле запроса
     * всегда выставляется верхнеуровневое {@code stream: true} (по спецификации API v2).
     */
    protected HttpRequest.HttpRequestBuilder prepareCompletionV2StreamHttpRequest(CompletionRequestV2 request,
            String sessionId) {
        CompletionRequestV2 forHttp = request;
        if (!Boolean.TRUE.equals(request.stream())) {
            forHttp = request.toBuilder().stream(true).build();
        }
        try {
            return HttpRequest.builder()
                    .url(apiV2Url + "/chat/completions")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM)
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .headerIf(sessionId != null, SESSION_ID_HEADER, sessionId)
                    .body(objectMapper.writeValueAsBytes(forHttp));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpRequest createEmbeddingHttpRequest(EmbeddingRequest request) {
        HttpRequestBuilder builder;
        try {
            builder = HttpRequest.builder()
                    .url(apiUrl + "/embeddings")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .body(objectMapper.writeValueAsBytes(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createUploadFileHttpRequest(UploadFileRequest request) {
        Objects.requireNonNull(request.purpose(), "purpose must not be null");
        Objects.requireNonNull(request.file(), "file must not be null");
        Objects.requireNonNull(request.mimeType(), "mimeType must not be null");
        Objects.requireNonNull(request.fileName(), "fileName must not be null");

        var boundary = Long.toHexString(System.currentTimeMillis());
        var requestBody = FileUtils.createMultiPartBody(request.file(), boundary, request.purpose(),
                request.mimeType(), request.fileName());

        var builder = HttpRequest.builder()
                .url(apiUrl + "/files")
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA + "; boundary=" + boundary)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .body(requestBody);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createDownloadFileHttpRequest(String fileId, String clientId) {
        Objects.requireNonNull(fileId, "fileId must not be null");

        var builder = HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId + "/content")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM)
                .headerIf(clientId != null, CLIENT_ID_HEADER, clientId);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createAvailableFileListHttpRequest() {
        var builder = HttpRequest.builder()
                .url(apiUrl + "/files")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createFileInfoHttpRequest(String fileId) {
        Objects.requireNonNull(fileId, "fileId must not be null");

        var builder = HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId)
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createDeleteFileHttpRequest(String fileId) {
        Objects.requireNonNull(fileId, "fileId must not be null");

        var builder = HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId + "/delete")
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createTokenCountHttpRequest(TokenCountRequest request) {
        HttpRequestBuilder builder;
        try {
            builder = HttpRequest.builder()
                    .url(apiUrl + "/tokens/count")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .body(objectMapper.writeValueAsBytes(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createBalanceHttpRequest() {
        var builder = HttpRequest.builder()
                .url(apiUrl + "/balance")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString());

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createBatchesHttpRequest(byte[] jsonlBody, BatchMethod method) {
        var builder = HttpRequest.builder()
                .url(apiUrl + "/batches?method=" + method.value())
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                .body(jsonlBody);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createBatchStatusHttpRequest(String batchId) {
        var url = batchId != null ? apiUrl + "/batches?batch_id=" + batchId : apiUrl + "/batches";

        var builder = HttpRequest.builder()
                .url(url)
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString());

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createFilterCheckHttpRequest(FilterCheckRequest request) {
        HttpRequestBuilder builder;
        try {
            builder = HttpRequest.builder()
                    .url(apiUrl + "/filter/check")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .body(objectMapper.writeValueAsBytes(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        authClient.authenticate(builder);

        return builder.build();
    }

    private SSL mapSslConfig(boolean verifySslCerts) {
        if (!verifySslCerts) {
            return SSL.builder().verifySslCerts(false).build();
        } else {
            return null;
        }
    }
}
