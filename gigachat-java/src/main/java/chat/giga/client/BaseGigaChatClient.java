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
import chat.giga.model.TokenCountRequest;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.file.UploadFileRequest;
import chat.giga.util.FileUtils;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static chat.giga.util.Utils.*;

import static java.time.Duration.ofSeconds;

abstract class BaseGigaChatClient {

    public static final String DEFAULT_API_URL = "https://gigachat.devices.sberbank.ru/api/v1";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String CLIENT_ID_HEADER = "X-Client-ID";
    public static final String USER_AGENT_NAME = "GigaChat-java-lib";

    protected static final int MAX_RETRIES = 1;

    protected final AuthClient authClient;
    protected final HttpClient httpClient;
    protected final String apiUrl;

    protected final ObjectMapper objectMapper = JsonUtils.objectMapper();

    protected BaseGigaChatClient(HttpClient apiHttpClient,
            AuthClient authClient,
            Integer readTimeout,
            Integer connectTimeout,
            String apiUrl,
            boolean logRequests,
            boolean logResponses) {
        Objects.requireNonNull(authClient, "authClient must not be null");
        this.apiUrl = getOrDefault(apiUrl, DEFAULT_API_URL);
        this.authClient = authClient;

        var client = authClient.supportsHttpClient() ? authClient.getHttpClient()
                : (apiHttpClient == null ? new JdkHttpClientBuilder()
                        .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                        .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
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

    protected HttpRequest createCompletionHttpRequest(CompletionRequest request) {
        HttpRequestBuilder builder;
        try {
            builder = HttpRequest.builder()
                    .url(apiUrl + "/chat/completions")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                    .body(objectMapper.writeValueAsBytes(request.toBuilder()
                            .stream(false)
                            .build()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createEmbendingHttpRequest(EmbeddingRequest request) {
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
        var boundary = Long.toHexString(System.currentTimeMillis());
        var requestBody = FileUtils.createMultiPartBody(request.file(), boundary, request.purpose(),
                request.mimeType(), request.fileName());

        var builder = HttpRequest.builder()
                .url(apiUrl + "/files")
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA + "; boundary=" + boundary)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .body(requestBody.toString().getBytes(StandardCharsets.UTF_8));

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createDownloadFileHttpRequest(String fileId, String clientId) {
        var builder = HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId + "/content")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM)
                .headerIf(clientId != null, CLIENT_ID_HEADER, clientId);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createListAvailableFileHttpRequest() {
        var builder = HttpRequest.builder()
                .url(apiUrl + "/files")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createFileInfoHttpRequest(String fileId) {
        var builder = HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId)
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        authClient.authenticate(builder);

        return builder.build();
    }

    protected HttpRequest createDeleteFileHttpRequest(String fileId) {
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
}
