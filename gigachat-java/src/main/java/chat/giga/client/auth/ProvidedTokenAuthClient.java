package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;

import java.util.Objects;

class ProvidedTokenAuthClient implements AuthClient {

    private final String accessToken;

    public ProvidedTokenAuthClient(String accessToken) {
        Objects.requireNonNull(accessToken, "accessToken must not be null");
        this.accessToken = accessToken;
    }

    @Override
    public boolean supportsHttpClient() {
        return false;
    }

    @Override
    public void authenticate(HttpRequestBuilder requestBuilder) {
        requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }

    @Override
    public HttpClient getHttpClient() {
        return null;
    }

    @Override
    public AccessToken getToken() {
        return new AccessToken(accessToken, null);
    }
}
