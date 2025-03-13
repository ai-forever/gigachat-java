package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.model.completion.CompletionRequest;

public class CompletionExample {

    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .verifySslCerts(false)
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey("key")
                                .build())
                        .build())
                .build();
        try {
            System.out.println(client.completions(CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT_MAX)
                    .message(ChatMessage.builder()
                            .content("Какие факторы влияют на стоимость страховки на дом?")
                            .role(Role.USER)
                            .build())
                    .build()));
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
