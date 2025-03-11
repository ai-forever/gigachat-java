package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static chat.giga.util.Utils.*;

class OAuthClient extends TokenBasedAuthClient implements AuthClient {

    public static final String RQ_UID_HEADER = "RqUID";
    private static final String DEFAULT_AUTH_URL = "https://ngw.devices.sberbank.ru:9443/api/v2";

    private final String clientId;
    private final String secret;
    private final Scope scope;
    private final HttpClient httpClient;
    private final String authApiUrl;

    private final ObjectMapper objectMapper = JsonUtils.objectMapper();

    public OAuthClient(chat.giga.http.client.HttpClient httpClient, String clientId, String secret,
            Scope scope, String authApiUrl) {
        this.httpClient = httpClient;
        this.clientId = clientId;
        this.secret = secret;
        this.scope = scope;
        this.authApiUrl = getOrDefault(authApiUrl, DEFAULT_AUTH_URL);
        validateParams();
    }

    private void validateParams() {
        Objects.requireNonNull(clientId, "clientId must not be null");
        Objects.requireNonNull(secret, "clientSecret must not be null");
        Objects.requireNonNull(scope, "scope must not be null");
    }

    @Override
    public boolean supportsHttpClient() {
        return false;
    }

    @Override
    public void authenticate(HttpRequestBuilder requestBuilder) {
        requestBuilder.header(HttpHeaders.AUTHORIZATION, getBearerAuth());
    }

    @Override
    public HttpClient getHttpClient() {
        return null;
    }

    public AccessTokenResponse oauth() {
        var httpRequest = getTokenRequest(authApiUrl + "/oauth", scope, clientId, secret)
                .header(RQ_UID_HEADER, UUID.randomUUID().toString())
                .build();
        try {
            var httpResponse = httpClient.execute(httpRequest);
            return objectMapper.readValue(httpResponse.body(), AccessTokenResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected AccessToken refreshToken() {
        var token = oauth();
        return new AccessToken(token.accessToken(), Instant.ofEpochMilli(token.expiresAt()));
    }

}
