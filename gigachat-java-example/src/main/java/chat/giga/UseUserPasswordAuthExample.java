package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.UserPasswordAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;

public class UseUserPasswordAuthExample {

    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .apiUrl("https://api.ru/v1")
                .logResponses(true)
                .logRequests(true)
                .authClient(AuthClient.builder()
                        .withUserPassword(
                                UserPasswordAuthBuilder.builder()
                                        .user("user")
                                        .password("password")
                                        .authApiUrl("https://api.ru/v1")
                                        .scope(Scope.GIGACHAT_API_PERS)
                                        .build()).build()
                )
                .build();
        try {
            System.out.println(client.completions(CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT_PRO)
                    .message(ChatMessage.builder()
                            .content("Как дела")
                            .role(ChatMessageRole.USER)
                            .build())
                    .build()));
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }

    }
}
