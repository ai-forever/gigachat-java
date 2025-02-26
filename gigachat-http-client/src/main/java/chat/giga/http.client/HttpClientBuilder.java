package chat.giga.http.client;

import java.time.Duration;

public interface HttpClientBuilder {

    Duration connectTimeout();

    HttpClientBuilder connectTimeout(Duration timeout);

    Duration readTimeout();

    HttpClientBuilder readTimeout(Duration timeout);

    HttpClient build();
}
