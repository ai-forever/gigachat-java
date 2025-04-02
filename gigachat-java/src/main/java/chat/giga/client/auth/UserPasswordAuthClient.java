package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import chat.giga.model.AccessTokenByUserPasswordResponse;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Objects;

class UserPasswordAuthClient extends TokenBasedAuthClient implements AuthClient {

    private final String user;
    private final String password;

    private final Scope scope;
    private final HttpClient httpClient;
    private final String authApiUrl;

    public UserPasswordAuthClient(HttpClient httpClient, String user, String password,
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
    public void authenticate(HttpRequestBuilder requestBuilder) {
        requestBuilder.header(HttpHeaders.AUTHORIZATION, getBearerAuth());
    }

    @Override
    public HttpClient getHttpClient() {
        return null;
    }

    @Override
    public AccessToken getToken() {
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
