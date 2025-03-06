package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;

import java.util.Objects;

class CertificatesAuthClientImpl implements AuthClient {

    private final HttpClient httpClient;

    public CertificatesAuthClientImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
        Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    @Override
    public boolean supportsHttpClient() {
        return true;
    }

    @Override
    public HttpRequestBuilder authenticateRequest(HttpRequestBuilder request) {
        return request;
    }

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }

}


