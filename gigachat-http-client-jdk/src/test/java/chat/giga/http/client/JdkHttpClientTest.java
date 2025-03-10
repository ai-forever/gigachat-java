package chat.giga.http.client;

import chat.giga.http.client.sse.SseListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdkHttpClientTest {

    @Mock
    java.net.http.HttpClient delegate;
    @Mock
    HttpResponse<InputStream> jdkResponse;
    @Mock
    SseListener sseListener;

    HttpClient httpClient;

    @BeforeEach
    void setUp() {
        var builder = mock(java.net.http.HttpClient.Builder.class);
        when(builder.build()).thenReturn(delegate);

        httpClient = JdkHttpClient.builder()
                .httpClientBuilder(builder)
                .build();
    }


    @Test
    void execute() throws Exception {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("test".getBytes())
                .build();

        var captor = ArgumentCaptor.forClass(java.net.http.HttpRequest.class);
        when(delegate.<InputStream>send(captor.capture(), any())).thenReturn(jdkResponse);
        when(jdkResponse.statusCode()).thenReturn(200);
        when(jdkResponse.headers()).thenReturn(HttpHeaders.of(headers, (hn, hv) -> true));
        when(jdkResponse.body()).thenReturn(new ByteArrayInputStream("ok".getBytes()));

        var response = httpClient.execute(request);

        assertThat(response).satisfies(r -> {
            assertThat(r.statusCode()).isEqualTo(200);
            assertThat(r.headers()).containsEntry("testHeader", List.of("testValue"));
            assertThat(new String(r.body(), StandardCharsets.UTF_8)).isEqualTo("ok");
        });

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.method()).isEqualTo("POST");
            assertThat(r.uri()).asString().isEqualTo("http://test");
            assertThat(r.headers().map()).containsEntry("testHeader", List.of("testValue"));
            assertThat(r.bodyPublisher().isPresent()).isTrue();
            assertThat(r.bodyPublisher().get().contentLength()).isNotZero();
        });
    }

    @Test
    void executeFailedWhenInvalidHttpStatus() throws Exception {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("test".getBytes())
                .build();

        when(delegate.<InputStream>send(any(), any())).thenReturn(jdkResponse);
        when(jdkResponse.statusCode()).thenReturn(400);
        when(jdkResponse.body()).thenReturn(new ByteArrayInputStream("error".getBytes()));

        assertThatExceptionOfType(HttpClientException.class)
                .isThrownBy(() -> httpClient.execute(request))
                .satisfies(e -> {
                    assertThat(e.statusCode()).isEqualTo(400);
                    assertThat(new String(e.body(), StandardCharsets.UTF_8)).isEqualTo("error");
                });
    }


    @Test
    void executeAsync() throws Exception {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("test".getBytes())
                .build();

        var captor = ArgumentCaptor.forClass(java.net.http.HttpRequest.class);
        when(delegate.<InputStream>sendAsync(captor.capture(), any()))
                .thenReturn(CompletableFuture.completedFuture(jdkResponse));
        when(jdkResponse.statusCode()).thenReturn(200);
        when(jdkResponse.headers()).thenReturn(HttpHeaders.of(headers, (hn, hv) -> true));
        when(jdkResponse.body()).thenReturn(new ByteArrayInputStream("ok".getBytes()));

        var response = httpClient.executeAsync(request).get();

        assertThat(response).satisfies(r -> {
            assertThat(r.statusCode()).isEqualTo(200);
            assertThat(r.headers()).containsEntry("testHeader", List.of("testValue"));
            assertThat(new String(r.body(), StandardCharsets.UTF_8)).isEqualTo("ok");
        });

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.uri()).asString().isEqualTo("http://test");
            assertThat(r.method()).isEqualTo("POST");
            assertThat(r.headers().map()).containsEntry("testHeader", List.of("testValue"));
            assertThat(r.bodyPublisher().isPresent()).isTrue();
            assertThat(r.bodyPublisher().get().contentLength()).isNotZero();
        });
    }

    @Test
    void executeAsyncFailedWhenInvalidHttpStatus() {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("test".getBytes())
                .build();

        when(delegate.<InputStream>sendAsync(any(), any())).thenReturn(CompletableFuture.completedFuture(jdkResponse));
        when(jdkResponse.statusCode()).thenReturn(500);
        when(jdkResponse.body()).thenReturn(new ByteArrayInputStream("error".getBytes()));

        assertThatThrownBy(() -> httpClient.executeAsync(request).get())
                .isInstanceOf(ExecutionException.class)
                .cause()
                .isInstanceOfSatisfying(HttpClientException.class, e -> {
                    assertThat(e.statusCode()).isEqualTo(500);
                    assertThat(e.body()).isNotEmpty();
                });
    }

    @Test
    void executeWithSse() {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("sse".getBytes())
                .build();

        var captor = ArgumentCaptor.forClass(java.net.http.HttpRequest.class);
        when(delegate.<InputStream>sendAsync(captor.capture(), any()))
                .thenReturn(CompletableFuture.completedFuture(jdkResponse));
        when(jdkResponse.statusCode()).thenReturn(200);
        when(jdkResponse.body()).thenReturn(new ByteArrayInputStream("data: testData".getBytes()));

        httpClient.executeAsync(request, sseListener);

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.uri()).asString().isEqualTo("http://test");
            assertThat(r.method()).isEqualTo("POST");
            assertThat(r.headers().map()).containsEntry("testHeader", List.of("testValue"));
            assertThat(r.bodyPublisher().isPresent()).isTrue();
            assertThat(r.bodyPublisher().get().contentLength()).isNotZero();
        });
    }

    @Test
    void executeWithSseFailedWhenInvalidStatusCode() {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("sse".getBytes())
                .build();

        when(delegate.<InputStream>sendAsync(any(), any())).thenReturn(CompletableFuture.completedFuture(jdkResponse));
        when(jdkResponse.body()).thenReturn(new ByteArrayInputStream("data: testData".getBytes()));
        when(jdkResponse.statusCode()).thenReturn(500);

        httpClient.executeAsync(request, sseListener);

        verify(sseListener, times(1)).onError(any(HttpClientException.class));
    }

    @Test
    void executeWithSseFailedWhenResponseTimeout() {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("sse".getBytes())
                .build();

        when(delegate.<InputStream>sendAsync(any(), any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpTimeoutException("timeout")));

        httpClient.executeAsync(request, sseListener);

        verify(sseListener, times(1)).onError(any(HttpTimeoutException.class));
        verify(sseListener, never()).onComplete();
    }
}
