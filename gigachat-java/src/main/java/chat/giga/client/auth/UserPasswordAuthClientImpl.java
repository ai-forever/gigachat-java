package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import chat.giga.model.AccessTokenByUserPasswordResponse;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Objects;

class UserPasswordAuthClientImpl extends TokenBasedAuth implements AuthClient {
    private final String user;
    private final String password;

    private final Scope scope;
    private final HttpClient httpClient;
    private final String authApiUrl;

    private final ObjectMapper objectMapper = JsonUtils.objectMapper();

    public UserPasswordAuthClientImpl(HttpClient httpClient, String user, String password,
            Scope scope, String authApiUrl) {
        this.httpClient = httpClient;
        this.user = user;
        this.password = password;
        this.scope = scope;
        this.authApiUrl = authApiUrl;
        validateParams();
    }

    private void validateParams() {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(password, "password must not be null");
        Objects.requireNonNull(scope, "scope must not be null");
        Objects.requireNonNull(authApiUrl, "authApiUrl must not be null");
    }

    @Override
    public boolean supportsHttpClient() {
        return false;
    }

    @Override
    public HttpRequestBuilder authenticateRequest(HttpRequestBuilder request) {
        return request.header(HttpHeaders.AUTHORIZATION, getBearerAuth());
    }

    @Override
    public HttpClient getHttpClient() {
        return null;
    }

    protected AccessToken refreshToken() {
        var token = oauth();
        return new AccessToken(token.accessToken(), Instant.ofEpochMilli(token.expiresAt()));
    }

    public AccessTokenResponse oauth() {

        var httpRequest = getTokenRequest(authApiUrl + "/token", scope, user, password)
                .build();

        try {
            var httpResponse = httpClient.execute(httpRequest);
            AccessTokenByUserPasswordResponse response = objectMapper.readValue(httpResponse.body(),
                    AccessTokenByUserPasswordResponse.class);
            return AccessTokenResponse.builder()
                    .accessToken(response.accessToken)
                    .expiresAt(response.expiresAt)
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
