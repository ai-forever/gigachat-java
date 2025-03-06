package chat.giga;


import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.SSL;
import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.model.completion.CompletionRequest;

import java.net.http.HttpClient;

public class UseCertificateAuthExample {

    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .apiUrl("https://api.ru/v1")
                .logResponses(true)
                .logRequests(true)
                .authClient(AuthClient.builder()
                        .withCertificatesAuth(new JdkHttpClientBuilder()
                                .httpClientBuilder(HttpClient.newBuilder())
                                .ssl(SSL.builder()
                                        .truststorePassword("password")
                                        .trustStoreType("PKCS12")
                                        .truststorePath("/Users/test/ssl/client_truststore.p12")
                                        .keystorePassword("password")
                                        .keystoreType("PKCS12")
                                        .keystorePath("/Users/test/ssl/client_keystore.p12")
                                        .build())
                                .build())
                        .build())
                .build();
        try {
            System.out.println(client.completions(CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT_PRO)
                    .message(ChatMessage.builder()
                            .content("Как дела")
                            .role(Role.USER)
                            .build())
                    .build()));
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
