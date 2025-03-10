package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;

import java.util.Objects;

class CertificateAuthClient implements AuthClient {

    private final HttpClient httpClient;

    public CertificateAuthClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    @Override
    public boolean supportsHttpClient() {
        return true;
    }

    @Override
    public void authenticate(HttpRequestBuilder requestBuilder) {
        //no op
    }

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }
}


