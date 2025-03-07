package chat.giga.client.auth;

import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import chat.giga.http.client.MediaType;
import chat.giga.model.Scope;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

import static chat.giga.http.client.HttpHeaders.USER_AGENT;
import static chat.giga.http.client.MediaType.APPLICATION_JSON;

abstract class TokenBasedAuth {

    public static final String USER_AGENT_NAME = "GigaChat-java-lib";

    private final AtomicReference<AccessToken> accessToken = new AtomicReference<>();

    protected HttpRequestBuilder getTokenRequest(String url, Scope scope, String user, String password) {
        var formData = "scope=" + scope.name();
        var credentials = user + ":" + password;
        var encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return HttpRequest.builder()
                .url(url)
                .method(HttpMethod.POST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_X_WWW_FORM_URLENCODED)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .header(USER_AGENT, USER_AGENT_NAME)
                .body(formData.getBytes(StandardCharsets.UTF_8));
    }

    protected String getBearerAuth() {
        return "Bearer " + retrieveTokenIfExpired();
    }

    protected String retrieveTokenIfExpired() {
        return accessToken.updateAndGet(t -> {
            var expiresAt = t == null ? null : t.expiresAt();
            return expiresAt != null && Instant.now().isBefore(expiresAt) ? t : refreshToken();
        }).token();
    }

    protected abstract AccessToken refreshToken();
}
