package chat.giga.client;

import chat.giga.http.client.*;
import chat.giga.http.client.sse.SseListener;
import chat.giga.model.Scope;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.completion.CompletionChunkResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.file.UploadFileRequest;
import chat.giga.util.FileUtils;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static chat.giga.util.Utils.getOrDefault;
import static java.time.Duration.ofSeconds;


abstract class BaseGigaChatClient {

    public static final String DEFAULT_API_URL = "https://gigachat.devices.sberbank.ru/api/v1";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String CLIENT_ID_HEADER = "X-Client-ID";
    public static final String USER_AGENT_NAME = "GigaChat-java-lib";

    protected final GigaChatAuthClient gigaChatAuthClient;
    protected final HttpClient httpClient;
    protected final String accessToken;
    protected final boolean useCertificateAuth;
    protected final String apiUrl;
    protected final ObjectMapper objectMapper = JsonUtils.objectMapper();

    protected BaseGigaChatClient(String clientId,
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
                                 boolean logResponses) {
        this.accessToken = accessToken;
        this.useCertificateAuth = useCertificateAuth;
        this.apiUrl = getOrDefault(apiUrl, DEFAULT_API_URL);

        validateParams(clientId, clientSecret, scope);

        var client = apiHttpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : apiHttpClient;

        if (logRequests || logResponses) {
            this.httpClient = new LoggingHttpClient(client, logRequests, logResponses);
        } else {
            this.httpClient = client;
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

    protected HttpRequest createModelHttpRequest() {
        return HttpRequest.builder()
                .url(apiUrl + "/models")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                .build();
    }

    protected HttpRequest createCompletionHttpRequest(CompletionRequest request) throws JsonProcessingException {
        return HttpRequest.builder()
                .url(apiUrl + "/chat/completions")
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                .body(objectMapper.writeValueAsBytes(request.toBuilder()
                        .stream(false)
                        .build()))
                .build();
    }

    protected void executeCompletionStream(CompletionRequest request,
                                           ResponseHandler<CompletionChunkResponse> handler) {
        try {
            var httpRequest = HttpRequest.builder()
                    .url(apiUrl + "/chat/completions")
                    .method(HttpMethod.POST)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM)
                    .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                    .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
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
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected HttpRequest createEmbendingHttpRequest(EmbeddingRequest request) throws JsonProcessingException {
        return HttpRequest.builder()
                .url(apiUrl + "/embeddings")
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                .body(objectMapper.writeValueAsBytes(request))
                .build();
    }

    protected HttpRequest createUploadFileHttpRequest(UploadFileRequest request) {
        var boundary = Long.toHexString(System.currentTimeMillis());
        var requestBody = FileUtils.createMultiPartBody(request.file(), boundary, request.purpose(),
                request.mimeType(), request.fileName());

        return HttpRequest.builder()
                .url(apiUrl + "/files")
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA + "; boundary=" + boundary)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .body(requestBody.toString().getBytes(StandardCharsets.UTF_8))
                .build();
    }

    protected HttpRequest createDownloadFileHttpRequest(String fileId, String clientId) {
        return HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId + "/content")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.IMAGE_JPG)
                .headerIf(clientId != null, CLIENT_ID_HEADER, clientId)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .build();
    }

    protected HttpRequest createListAvailableFileHttpRequest() {
        return HttpRequest.builder()
                .url(apiUrl + "/files")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .build();
    }

    protected HttpRequest createFileInfoHttpRequest(String fileId) {
        return HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId)
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .build();
    }

    protected HttpRequest createDeleteFileHttpRequest(String fileId) {
        return HttpRequest.builder()
                .url(apiUrl + "/files/" + fileId + "/delete")
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .build();
    }

    protected HttpRequest createTokenCountHttpRequest(TokenCountRequest request) throws JsonProcessingException {
        return HttpRequest.builder()
                .url(apiUrl + "/tokens/count")
                .method(HttpMethod.POST)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                .body(objectMapper.writeValueAsBytes(request))
                .build();
    }

    protected HttpRequest createBalanceHttpRequest() {
        return HttpRequest.builder()
                .url(apiUrl + "/balance")
                .method(HttpMethod.GET)
                .header(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .headerIf(!useCertificateAuth, HttpHeaders.AUTHORIZATION, buildBearerAuth())
                .header(REQUEST_ID_HEADER, UUID.randomUUID().toString())
                .build();
    }

    private String getAccessToken() {
        return accessToken == null && !useCertificateAuth ? gigaChatAuthClient.retrieveTokenIfExpired() : accessToken;
    }

    private String buildBearerAuth() {
        return "Bearer " + getAccessToken();
    }
}
