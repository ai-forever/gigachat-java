package chat.giga.http.client;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class JdkHttpClientBuilderTest {

    HttpClientBuilder builder = new JdkHttpClientBuilder();

    @Test
    void build() {
        var client = builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .build();

        assertThat(client).isNotNull();
    }
}
