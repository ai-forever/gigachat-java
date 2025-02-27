package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.model.*;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static chat.giga.client.Utils.getOrDefault;
import static java.time.Duration.ofSeconds;


public class GigaChatDefaultClient implements GigaChatClient {

    private static final String DEFAULT_API_URL = "https://gigachat.devices.sberbank.ru/api/v1";
    private GigaChatAuthClient gigaChatAuthClient;
    private chat.giga.http.client.HttpClient httpClient;
    private chat.giga.http.client.HttpClient authHtpClient;
    private String token;
    private String apiUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Builder
    public GigaChatDefaultClient(String clientId,
            String clientSecret,
            Scope scope,
            String token,
            HttpClient apiHttpClient,
            HttpClient authHtpClient,
            Integer readTimeout,
            Integer connectTimeout,
            String apiUrl,
            String authApiUrl
    ) {


        this.token = token;
        this.apiUrl = apiUrl;
        this.httpClient = apiHttpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : apiHttpClient;
        this.authHtpClient = authHtpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(getOrDefault(readTimeout, 15)))
                .connectTimeout(ofSeconds(getOrDefault(connectTimeout, 15)))
                .build() : authHtpClient;
        this.gigaChatAuthClient = new GigaChatAuthClientImpl(this.authHtpClient, clientId, clientSecret, scope,
                authApiUrl);
    }

    @Override
    public ModelsResponse models() {
        var request = HttpRequest.builder()
                .url(getApiUrl() + "/models")
                .method(HttpMethod.GET)
                .header("Authorization", "Bearer " + getToken())
                .header("Accept", "application/json")
                .build();

        var response = httpClient.execute(request);

        try {
            return objectMapper.readValue(response.bodyAsString(), ModelsResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getApiUrl() {
        return getOrDefault(this.apiUrl, DEFAULT_API_URL);
    }

    private String getToken() {
        return token == null ? gigaChatAuthClient.retrieveTokenIfExpired() : token;
    }

    @Override
    public CompletionsResponse completions(CompletionsRequest request) {
        return null;
    }

    @Override
    public EmbeddingResponse embeddings(EmbeddingRequest request) {
        var rqUID = UUID.randomUUID().toString();
        var rq = HttpRequest.builder()
                .url(getApiUrl() + "/embeddings")
                .method(HttpMethod.POST)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("RqUID", rqUID)
                .header("Authorization", "Bearer " + getToken())
                .body(new ByteArrayInputStream(request.toString().getBytes(StandardCharsets.UTF_8)))
                .build();
        try {
            var response = httpClient.execute(rq);
            return objectMapper.readValue(response.body(), EmbeddingResponse.class);
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
    public TokensCountResponse tokensCount(TokensCountResponse request) {
        return null;
    }


}
