package chat.giga.client;

import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpResponse;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static chat.giga.client.Utils.getOrDefault;

public class GigaChatAuthClientImpl implements GigaChatAuthClient {

    private static final String DEFAULT_AUTH_URL = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";
    protected final AtomicReference<AccessToken> accessToken = new AtomicReference<>();
    private String clientId;
    private String secret;
    private Scope scope;
    private chat.giga.http.client.HttpClient httpClient;
    private String authApiUrl;
    private ObjectMapper objectMapper = new ObjectMapper();

    public GigaChatAuthClientImpl(chat.giga.http.client.HttpClient httpClient, String clientId, String secret, Scope scope, String authApiUrl) {
        this.httpClient = httpClient;
        this.clientId = clientId;
        this.secret = secret;
        this.scope = scope;
        this.authApiUrl = authApiUrl;
    }

    public String retrieveTokenIfExpired() {
        return accessToken.updateAndGet(t -> {
            Instant expiresAt = t == null ? null : t.getExpiresAt();
            return expiresAt != null && Instant.now().isBefore(expiresAt) ? t : refreshToken();
        }).getToken();
    }

    public AccessTokenResponse oauth() {
        String formData = "scope=" + scope.name();
        String credentials = clientId + ":" + secret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String rqUID = UUID.randomUUID().toString();

        var request = HttpRequest.builder()
                .url(getApiUrl())
                .method(HttpMethod.POST)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .header("RqUID", rqUID)
                .header("Authorization", "Basic " + encodedCredentials)
                .body(new ByteArrayInputStream(formData.getBytes(StandardCharsets.UTF_8)))
                .build();

        HttpResponse response;
        try {
            response = httpClient.execute(request);
            return objectMapper.readValue(response.bodyAsString(), AccessTokenResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AccessToken refreshToken() {
        AccessTokenResponse token = oauth();
        return new AccessToken(token.getAccessToken(), Instant.ofEpochMilli(token.getExpiresAt()));
    }

    private String getApiUrl() {
        return getOrDefault(this.authApiUrl, DEFAULT_AUTH_URL);
    }
}
