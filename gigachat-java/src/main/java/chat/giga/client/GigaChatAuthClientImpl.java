package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static chat.giga.client.GigaChatClientImpl.USER_AGENT;
import static chat.giga.client.GigaChatClientImpl.USER_AGENT_VALUE;
import static chat.giga.http.client.MediaType.APPLICATION_JSON;
import static chat.giga.util.Utils.getOrDefault;

public class GigaChatAuthClientImpl implements GigaChatAuthClient {

    private static final String DEFAULT_AUTH_URL = "https://ngw.devices.sberbank.ru:9443/api/v2";

    private final String clientId;
    private final String secret;
    private final Scope scope;
    private final HttpClient httpClient;
    private final String authApiUrl;

    private final ObjectMapper objectMapper = JsonUtils.objectMapper();
    private final AtomicReference<AccessToken> accessToken = new AtomicReference<>();

    public GigaChatAuthClientImpl(chat.giga.http.client.HttpClient httpClient, String clientId, String secret,
            Scope scope, String authApiUrl) {
        this.httpClient = httpClient;
        this.clientId = clientId;
        this.secret = secret;
        this.scope = scope;
        this.authApiUrl = getOrDefault(authApiUrl, DEFAULT_AUTH_URL);
    }

    public String retrieveTokenIfExpired() {
        return accessToken.updateAndGet(t -> {
            var expiresAt = t == null ? null : t.expiresAt();
            return expiresAt != null && Instant.now().isBefore(expiresAt) ? t : refreshToken();
        }).token();
    }

    public AccessTokenResponse oauth() {
        var formData = "scope=" + scope.name();
        var credentials = clientId + ":" + secret;
        var encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        var httpRequest = HttpRequest.builder()
                .url(authApiUrl + "/oauth")
                .method(HttpMethod.POST)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .header("RqUID", UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .header(USER_AGENT, USER_AGENT_VALUE)
                .body(formData.getBytes(StandardCharsets.UTF_8))
                .build();

        try {
            var httpResponse = httpClient.execute(httpRequest);
            return objectMapper.readValue(httpResponse.body(), AccessTokenResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AccessToken refreshToken() {
        var token = oauth();
        return new AccessToken(token.accessToken(), Instant.ofEpochMilli(token.expiresAt()));
    }

}
