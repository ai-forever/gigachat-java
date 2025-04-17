package chat.giga.http.client;

import java.time.Duration;
import java.util.Map;

public interface HttpClientBuilder {

    Duration connectTimeout();

    HttpClientBuilder connectTimeout(Duration timeout);

    Duration readTimeout();

    HttpClientBuilder readTimeout(Duration timeout);

    HttpClient build();

    SSL ssl();

    HttpClientBuilder ssl(SSL ssl);

    Map<String, String> customHeaders();

    HttpClientBuilder customHeaders(Map<String, String> customHeaders);
}
