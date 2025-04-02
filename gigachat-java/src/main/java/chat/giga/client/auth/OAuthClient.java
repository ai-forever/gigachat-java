package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;

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
    private final String authKey;
    private final Scope scope;
    private final HttpClient httpClient;
    private final String authApiUrl;

    public OAuthClient(chat.giga.http.client.HttpClient httpClient, String clientId,  String secret, String authKey,
            Scope scope, String authApiUrl) {
        this.httpClient = httpClient;
        this.clientId = clientId;
        this.secret = secret;
        this.authKey  = authKey;
        this.scope = scope;
        this.authApiUrl = getOrDefault(authApiUrl, DEFAULT_AUTH_URL);
        validateParams();
    }

    private void validateParams() {
        if (authKey == null) {
            Objects.requireNonNull(clientId, "clientId must not be null");
            Objects.requireNonNull(secret, "clientSecret must not be null");
        }
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
        var httpRequest = (authKey == null ? getTokenRequest(authApiUrl + "/oauth", scope, clientId, secret)
                : getTokenRequest(authApiUrl + "/oauth", scope, authKey))
                .header(RQ_UID_HEADER, UUID.randomUUID().toString())
                .build();
        try {
            var httpResponse = httpClient.execute(httpRequest);
            return objectMapper.readValue(httpResponse.body(), AccessTokenResponse.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public AccessToken getToken() {
        var token = oauth();
        return new AccessToken(token.accessToken(), Instant.ofEpochMilli(token.expiresAt()));
    }

}
