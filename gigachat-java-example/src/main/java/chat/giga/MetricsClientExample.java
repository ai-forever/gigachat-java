package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.SSL;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.java11.instrument.binder.jdk.MicrometerHttpClient;

import java.net.http.HttpClient;
import java.util.function.Function;

public class MetricsClientExample {
    public static void main(String[] args) {

        SimpleMeterRegistry oneSimpleMeter = new SimpleMeterRegistry();
        Function<HttpClient, HttpClient> metricsDecorator = client -> MicrometerHttpClient.instrumentationBuilder(client, oneSimpleMeter)
                // можно кастомизировать через .customObservationConvention
                .uriMapper((request) -> request.uri().toString())
                .build();

        GigaChatClient client = GigaChatClient.builder()
                // при включении логирования используется LoggingHttpClient который при подключении  зависимости ch.qos.logback:logback-classic пишет в logback
                .logRequests(true)
                .logResponses(true)
                .authClient(AuthClient.builder()
                        .withCertificatesAuth(new JdkHttpClientBuilder()
                                .decorator(metricsDecorator)
                                .ssl(SSL.builder()
                                        .verifySslCerts(false)
                                        .keystorePassword("pass")
                                        .keystoreType("PKCS12")
                                        .keystorePath("/Users/test/ssl/client_keystore.p12")
                                        .build())
                                .build())
                        .build())
                .apiUrl("test.ru/v1")
                .build();
        try {
            System.out.println(client.models());

            System.out.println("metrics: " + oneSimpleMeter.getMetersAsString());

        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
