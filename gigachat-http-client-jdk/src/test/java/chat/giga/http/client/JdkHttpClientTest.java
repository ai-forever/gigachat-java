package chat.giga.http.client;

import chat.giga.http.client.sse.SseListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
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

    @TempDir
    File tempDir;

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
    void executeFailedWhenClientError() throws Exception {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("test".getBytes())
                .build();

        when(delegate.<InputStream>send(any(), any())).thenThrow(new IOException());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> httpClient.execute(request))
                .withCauseInstanceOf(IOException.class);
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
    void executeWithSse() throws Exception {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("sse".getBytes())
                .build();

        var captor = ArgumentCaptor.forClass(java.net.http.HttpRequest.class);
        when(delegate.<InputStream>send(captor.capture(), any())).thenReturn(jdkResponse);
        when(jdkResponse.statusCode()).thenReturn(200);
        when(jdkResponse.body()).thenReturn(new ByteArrayInputStream("data: testData".getBytes()));

        httpClient.execute(request, sseListener);

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.uri()).asString().isEqualTo("http://test");
            assertThat(r.method()).isEqualTo("POST");
            assertThat(r.headers().map()).containsEntry("testHeader", List.of("testValue"));
            assertThat(r.bodyPublisher().isPresent()).isTrue();
            assertThat(r.bodyPublisher().get().contentLength()).isNotZero();
        });
    }

    @Test
    void executeWithSseFailedWhenInvalidStatusCode() throws Exception {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("sse".getBytes())
                .build();

        when(delegate.<InputStream>send(any(), any())).thenReturn(jdkResponse);
        when(jdkResponse.body()).thenReturn(new ByteArrayInputStream("data: testData".getBytes()));
        when(jdkResponse.statusCode()).thenReturn(500);

        httpClient.execute(request, sseListener);

        verify(sseListener, times(1)).onError(any(HttpClientException.class));
    }

    @Test
    void executeWithSseFailedWhenClientError() throws Exception {
        var headers = Map.of("testHeader", List.of("testValue"));
        var request = HttpRequest.builder()
                .url("http://test")
                .method(HttpMethod.POST)
                .headers(headers)
                .body("sse".getBytes())
                .build();

        when(delegate.<InputStream>send(any(), any())).thenThrow(new RuntimeException());

        httpClient.execute(request, sseListener);

        verify(sseListener, times(1)).onError(any(RuntimeException.class));
        verify(sseListener, never()).onComplete();
    }

    @Test
    void customHeaders() throws IOException, InterruptedException {
        var builder = mock(java.net.http.HttpClient.Builder.class);
        when(builder.build()).thenReturn(delegate);

        httpClient = JdkHttpClient.builder()
                .httpClientBuilder(builder)
                .customHeaders(Map.of("customHeaderKey","customHeaderKey"))
                .build();


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

        httpClient.execute(request);

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.headers().map()).containsEntry("customHeaderKey", List.of("customHeaderKey"));
        });

    }

    @Test
    void sslContextShouldBeBuildOnce() {
        var keyStore = new File(tempDir, "keystore.p12");
        var keyStoreType = "PKCS12";
        var keyStorePassword = "123456";

        SSLTestUtils.createKeyStore(
                keyStore,
                keyStoreType,
                "CN=Test, OU=Test, O=Test, C=Test",
                "test",
                keyStorePassword,
                "123456"
        );

        var builder = mock(java.net.http.HttpClient.Builder.class);
        when(builder.build()).thenReturn(delegate);

        var ssl = SSL.builder()
                .verifySslCerts(false)
                .keystorePath(keyStore.getPath())
                .keystoreType(keyStoreType)
                .keystorePassword(keyStorePassword)
                .build();

        httpClient = JdkHttpClient.builder()
                .httpClientBuilder(builder)
                .ssl(ssl)
                .build();

        verify(builder, times(1)).sslContext(any());
    }
}
