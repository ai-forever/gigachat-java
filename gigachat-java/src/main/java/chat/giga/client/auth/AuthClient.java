package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpRequest;

public interface AuthClient {

    void authenticate(HttpRequest.HttpRequestBuilder requestBuilder);

    boolean supportsHttpClient();

    HttpClient getHttpClient();

    static AuthClientBuilder builder() {
        return new AuthClientBuilder();
    }
}
