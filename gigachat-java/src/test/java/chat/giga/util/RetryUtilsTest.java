package chat.giga.util;

import chat.giga.http.client.HttpClientException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RetryUtilsTest {

    @Test
    void retry401WhenRetriesExhausted() {
        assertThatThrownBy(() -> RetryUtils.retry401(() -> {
            throw new HttpClientException(401, null);
        }, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Retries exhausted after 1 attempts");
    }

    @Test
    void retry401FailedWhenStatusIsNot401() {
        assertThatThrownBy(() -> RetryUtils.retry401(() -> {
            throw new HttpClientException(400, null);
        }, 1)).isInstanceOf(HttpClientException.class);
    }

    @Test
    void retry401FailedWhenInvalidException() {
        assertThatThrownBy(() -> RetryUtils.retry401(() -> {
            throw new RuntimeException();
        }, 1)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void retry401AsyncWhenRetriesExhausted() {
        var attempts = new AtomicInteger();
        assertThatThrownBy(() -> RetryUtils.retry401Async(() -> {
            attempts.getAndIncrement();
            return CompletableFuture.failedFuture(new HttpClientException(401, null));
        }, 1).get()).hasCauseInstanceOf(HttpClientException.class);

        assertThat(attempts.get()).isEqualTo(2);
    }

    @Test
    void retry401AsyncFailedWhenStatusIsNot401() {
        var attempts = new AtomicInteger();
        assertThatThrownBy(() -> RetryUtils.retry401Async(() -> {
            attempts.getAndIncrement();
            return CompletableFuture.failedFuture(new HttpClientException(400, null));
        }, 1).get()).hasCauseInstanceOf(HttpClientException.class);

        assertThat(attempts.get()).isEqualTo(1);
    }

    @Test
    void retry401AsyncFailedWhenInvalidException() {
        var attempts = new AtomicInteger();
        assertThatThrownBy(() -> RetryUtils.retry401Async(() -> {
            attempts.getAndIncrement();
            return CompletableFuture.failedFuture(new RuntimeException());
        }, 1).get()).hasCauseInstanceOf(RuntimeException.class);

        assertThat(attempts.get()).isEqualTo(1);
    }
}
