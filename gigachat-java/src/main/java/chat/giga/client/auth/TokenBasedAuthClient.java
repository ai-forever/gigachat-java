package chat.giga.client.auth;

import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import chat.giga.http.client.MediaType;
import chat.giga.model.Scope;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static chat.giga.http.client.HttpHeaders.USER_AGENT;
import static chat.giga.http.client.MediaType.APPLICATION_JSON;

abstract class TokenBasedAuthClient {

    public static final String USER_AGENT_NAME = "GigaChat-java-lib";

    protected final ObjectMapper objectMapper = JsonUtils.objectMapper();

    private final AtomicReference<AccessToken> accessToken = new AtomicReference<>();
    private final ReentrantLock lock = new ReentrantLock();

    protected HttpRequestBuilder getTokenRequest(String url, Scope scope, String user, String password) {
        var credentials = user + ":" + password;
        var encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return getTokenRequest(url, scope, encodedCredentials);
    }

    protected HttpRequestBuilder getTokenRequest(String url, Scope scope, String key) {
        var formData = "scope=" + scope.name();
        return HttpRequest.builder()
                .url(url)
                .method(HttpMethod.POST)
                .header(USER_AGENT, USER_AGENT_NAME)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + key)
                .body(formData.getBytes(StandardCharsets.UTF_8));
    }

    protected String getBearerAuth() {
        return "Bearer " + retrieveTokenIfExpired();
    }

    protected String retrieveTokenIfExpired() {
        AccessToken token = accessToken.get();
        if (token == null || Instant.now().isAfter(token.expiresAt())) {
            lock.lock();
            try {
                token = accessToken.get();
                if (token == null || Instant.now().isAfter(token.expiresAt())) {
                    token = getToken();
                    accessToken.set(token);
                }
            } finally {
                lock.unlock();
            }
        }
        return token.token();
    }

    protected abstract AccessToken getToken();
}
