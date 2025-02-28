package chat.giga.http.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class JdkHttpClient implements HttpClient {

    private final java.net.http.HttpClient delegate;
    private final Duration readTimeout;

    public JdkHttpClient(JdkHttpClientBuilder builder) {
        var httpClientBuilder = builder.httpClientBuilder();

        if (builder.connectTimeout() != null) {
            httpClientBuilder.connectTimeout(builder.connectTimeout());
        }
        if (builder.ssl() != null && !builder.ssl().verifySslCerts()) {
            httpClientBuilder.sslContext(disableSSLVerification(builder.ssl()));
        }
        if (builder.ssl() != null && (builder.ssl().keystorePath() != null || builder.ssl().truststorePath() != null)) {
            httpClientBuilder.sslContext(createSSLContext(builder.ssl()));
        }

        this.delegate = httpClientBuilder.build();
        this.readTimeout = builder.readTimeout();
    }

    public static JdkHttpClientBuilder builder() {
        return new JdkHttpClientBuilder();
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        try {
            var jdkResponse = delegate.send(mapJdkRequest(request), BodyHandlers.ofInputStream());

            if (!isSuccessful(jdkResponse)) {
                throw new HttpClientException(jdkResponse.statusCode(), jdkResponse.body());
            }

            return mapResponse(jdkResponse);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
        var jdkRequest = mapJdkRequest(request);

        return delegate.sendAsync(jdkRequest, BodyHandlers.ofInputStream())
                .thenApply(r -> {
                    if (!isSuccessful(r)) {
                        throw new HttpClientException(r.statusCode(), r.body());
                    }
                    return mapResponse(r);
                });
    }

    private java.net.http.HttpRequest mapJdkRequest(HttpRequest request) {
        var builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(request.url()));

        request.headers().forEach((name, values) -> {
            if (values != null) {
                values.forEach(value -> builder.header(name, value));
            }
        });

        BodyPublisher bodyPublisher;
        if (request.body() != null) {
            bodyPublisher = BodyPublishers.ofInputStream(request::body);
        } else {
            bodyPublisher = BodyPublishers.noBody();
        }
        builder.method(request.method().name(), bodyPublisher);

        if (readTimeout != null) {
            builder.timeout(readTimeout);
        }

        return builder.build();
    }

    private static boolean isSuccessful(java.net.http.HttpResponse<InputStream> jdkResponse) {
        var statusCode = jdkResponse.statusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    private static HttpResponse mapResponse(java.net.http.HttpResponse<InputStream> jdkResponse) {
        return HttpResponse.builder()
                .statusCode(jdkResponse.statusCode())
                .headers(jdkResponse.headers().map())
                .body(jdkResponse.body())
                .build();
    }

    public static SSLContext disableSSLVerification(SSL ssl) {
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
            var sslContext = SSLContext.getInstance(ssl.protocol());
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SSLContext createSSLContext(SSL ssl) {
        try {
            KeyManagerFactory keyManagerFactory = null;

            if (ssl.keystorePath() != null) {
                Objects.requireNonNull(ssl.keystorePassword(), "keystorePassword must not be null");

                keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

                var keyStore = KeyStore.getInstance(ssl.keystoreType());
                try (var keyStoreStream = new FileInputStream(ssl.keystorePath())) {
                    keyStore.load(keyStoreStream, ssl.keystorePassword().toCharArray());
                }
                keyManagerFactory.init(keyStore, ssl.keystorePassword().toCharArray());
            }
            TrustManagerFactory trustManagerFactory = null;
            if (ssl.truststorePath() != null) {
                Objects.requireNonNull(ssl.truststorePassword(), "truststorePassword must not be null");

                trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

                var trustStore = KeyStore.getInstance(ssl.trustStoreType());
                try (var trustStoreStream = new FileInputStream(ssl.truststorePath())) {
                    trustStore.load(trustStoreStream, ssl.truststorePassword().toCharArray());
                }
                trustManagerFactory.init(trustStore);
            }

            var sslContext = SSLContext.getInstance(ssl.protocol());
            sslContext.init(keyManagerFactory != null ? keyManagerFactory.getKeyManagers() : null,
                    trustManagerFactory != null ? trustManagerFactory.getTrustManagers() : null, null);
            return sslContext;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
