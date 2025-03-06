package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;

import java.util.Objects;

class ProvidedTokenAuthClientImpl implements AuthClient {

    String accessToken;

    public ProvidedTokenAuthClientImpl(String accessToken) {
        this.accessToken = accessToken;
        Objects.requireNonNull(accessToken, "accessToken must not be null");
    }

    @Override
    public boolean supportsHttpClient() {
        return false;
    }

    @Override
    public HttpRequestBuilder authenticateRequest(HttpRequestBuilder request) {
        return request.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }

    @Override
    public HttpClient getHttpClient() {
        return null;
    }

}
