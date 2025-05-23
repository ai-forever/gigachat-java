package chat.giga.http.client;

import chat.giga.http.client.sse.SseListener;
import chat.giga.http.client.sse.SseParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class JdkHttpClient implements HttpClient {

    private final java.net.http.HttpClient delegate;
    private final Duration readTimeout;
    private final Map<String, String> customHeaders;

    public JdkHttpClient(JdkHttpClientBuilder builder) {
        var httpClientBuilder = builder.httpClientBuilder();

        if (builder.connectTimeout() != null) {
            httpClientBuilder.connectTimeout(builder.connectTimeout());
        }
        if (builder.ssl() != null && (!builder.ssl().verifySslCerts() || builder.ssl().keystorePath() != null || builder.ssl().truststorePath() != null)) {
            httpClientBuilder.sslContext(createSSLContext(builder.ssl()));
        }
        if(builder.customHeaders() != null && !builder.customHeaders().isEmpty()){
            this.customHeaders = builder.customHeaders();
        } else {
            this.customHeaders = Map.of();
        }

        java.net.http.HttpClient baseClient = httpClientBuilder.build();

        if (builder.decorator() != null) {
            this.delegate = builder.decorator().apply(baseClient);
        } else {
            this.delegate = baseClient;
        }

        this.readTimeout = builder.readTimeout();
    }

    public static JdkHttpClientBuilder builder() {
        return new JdkHttpClientBuilder();
    }

    private static HttpResponse mapResponse(java.net.http.HttpResponse<InputStream> jdkResponse) {
        try (var body = jdkResponse.body()) {
            return HttpResponse.builder()
                    .statusCode(jdkResponse.statusCode())
                    .headers(jdkResponse.headers().map())
                    .body(body.readAllBytes())
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        try {
            var jdkResponse = delegate.send(mapJdkRequest(request), BodyHandlers.ofInputStream());

            if (!isSuccessful(jdkResponse)) {
                throw getClientException(jdkResponse);
            }

            return mapResponse(jdkResponse);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClientException getClientException(java.net.http.HttpResponse<InputStream> jdkResponse) {
        try (var body = jdkResponse.body()) {
            return new HttpClientException(jdkResponse.statusCode(), body.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void execute(HttpRequest request, SseListener listener) {
        var jdkRequest = mapJdkRequest(request);

        var parser = new SseParser(listener);
        try {
            var jdkResponse = delegate.send(jdkRequest, BodyHandlers.ofInputStream());

            if (!isSuccessful(jdkResponse)) {
                listener.onError(getClientException(jdkResponse));
                return;
            }

            parser.parse(jdkResponse.body());
        } catch (Exception e) {
            listener.onError(e);
        }
    }

    @Override
    public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
        var jdkRequest = mapJdkRequest(request);

        return delegate.sendAsync(jdkRequest, BodyHandlers.ofInputStream())
                .thenApply(r -> {
                    if (!isSuccessful(r)) {
                        throw getClientException(r);
                    }
                    return mapResponse(r);
                });
    }

    private static boolean isSuccessful(java.net.http.HttpResponse<InputStream> jdkResponse) {
        var statusCode = jdkResponse.statusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    private java.net.http.HttpRequest mapJdkRequest(HttpRequest request) {
        var builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(request.url()));

        request.headers().forEach((name, values) -> {
            if (values != null) {
                values.forEach(value -> builder.header(name, value));
            }
        });
        customHeaders.forEach((name, value) -> {
            if (value != null) {
                builder.header(name, value);
            }
        });

        BodyPublisher bodyPublisher;
        if (request.body() != null) {
            bodyPublisher = BodyPublishers.ofByteArray(request.body());
        } else {
            bodyPublisher = BodyPublishers.noBody();
        }
        builder.method(request.method().name(), bodyPublisher);

        if (readTimeout != null) {
            builder.timeout(readTimeout);
        }

        return builder.build();
    }

    private static TrustManager[] disableSSLVerification() {
        try {
            var trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            return trustAllCerts;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static SSLContext createSSLContext(SSL ssl) {
        try {
            KeyManager[] keyManagers = null;

            if (ssl.keystorePath() != null) {
                Objects.requireNonNull(ssl.keystorePassword(), "keystorePassword must not be null");

                var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

                var keyStore = KeyStore.getInstance(ssl.keystoreType());
                try (var keyStoreStream = new FileInputStream(ssl.keystorePath())) {
                    keyStore.load(keyStoreStream, ssl.keystorePassword().toCharArray());
                }
                keyManagerFactory.init(keyStore, ssl.keystorePassword().toCharArray());
                keyManagers = keyManagerFactory.getKeyManagers();
            }

            TrustManager[] trustManagers = null;
            if (!ssl.verifySslCerts()) {
                trustManagers = disableSSLVerification();
            }
            else if (ssl.truststorePath() != null) {
                Objects.requireNonNull(ssl.truststorePassword(), "truststorePassword must not be null");

                var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

                var trustStore = KeyStore.getInstance(ssl.trustStoreType());
                try (var trustStoreStream = new FileInputStream(ssl.truststorePath())) {
                    trustStore.load(trustStoreStream, ssl.truststorePassword().toCharArray());
                }
                trustManagerFactory.init(trustStore);
                trustManagers = trustManagerFactory.getTrustManagers();
            }

            var sslContext = SSLContext.getInstance(ssl.protocol());
            sslContext.init(keyManagers, trustManagers, null);
            return sslContext;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
