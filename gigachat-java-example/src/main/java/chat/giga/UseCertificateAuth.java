package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.SSL;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.model.completion.CompletionRequest;

import java.net.http.HttpClient;

public class UseCertificateAuth {

    public static void main(String[] args) {
        GigaChatClient client = GigaChatClient.builder()
                .useCertificateAuth(true)
                .apiUrl("https://internal-url.sber.ru")
                .apiHttpClient(new JdkHttpClientBuilder()
                        .httpClientBuilder(HttpClient.newBuilder())
                        .ssl(SSL.builder()
                                .truststorePassword("password")
                                .trustStoreType("PKCS12")
                                .truststorePath("/Users/user/ssl/client_truststore.p12")
                                .keystorePassword("password")
                                .keystoreType("PKCS12")
                                .keystorePath("/Users/user/ssl/client_keystore.p12")
                                .build())
                        .build())
                .scope(Scope.GIGACHAT_API_PERS)
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
