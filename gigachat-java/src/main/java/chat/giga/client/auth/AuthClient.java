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

    AccessToken getToken();

    /**
     * Закрыть клиент аутентификации и освободить ресурсы. По умолчанию операция пустая.
     * <p>Внимание: реализации {@code OAuthClient} и {@code UserPasswordAuthClient} при закрытии
     * закрывают переданный извне {@code HttpClient}. Если вы используете общий {@code HttpClient},
     * учтите, что вызов {@code close()} приведёт к его закрытию.
     */
    default void close() {
    }
}
