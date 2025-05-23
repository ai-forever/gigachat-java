package chat.giga.http.client;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

public interface HttpClientBuilder<T> {

    Duration connectTimeout();

    HttpClientBuilder<T> connectTimeout(Duration timeout);

    Duration readTimeout();

    HttpClientBuilder<T> readTimeout(Duration timeout);

    HttpClient build();

    SSL ssl();

    HttpClientBuilder<T> ssl(SSL ssl);

    Map<String, String> customHeaders();

    HttpClientBuilder<T> customHeaders(Map<String, String> customHeaders);

    Function<T, T> decorator();

    HttpClientBuilder<T> decorator(Function<T, T> decorator);
}
