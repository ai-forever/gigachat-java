package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpRequest;

public interface AuthClient {

    static AuthClientBuilder builder() {
        return new AuthClientBuilder();
    }

    HttpRequest.HttpRequestBuilder authenticateRequest(HttpRequest.HttpRequestBuilder request);

    boolean supportsHttpClient();

    HttpClient getHttpClient();
}
