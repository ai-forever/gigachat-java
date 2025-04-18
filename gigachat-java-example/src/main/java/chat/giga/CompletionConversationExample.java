package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.completion.ChatMessageRole;

public class CompletionConversationExample {

    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .logRequests(true)
                .logResponses(true)
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId("test-client-id")
                                .clientSecret("test-scope")
                                .build())
                        .build())
                .build();

        CompletionRequest.CompletionRequestBuilder builder = CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT_PRO)
                .message(ChatMessage.builder()
                        .content("Отвечай как программист")
                        .role(ChatMessageRole.SYSTEM)
                        .build())
                .message(ChatMessage.builder()
                        .content("Как спроектировать идеальный SDK?")
                        .role(ChatMessageRole.USER).build());

        try {
            for (int i = 0; i < 4; i++) {
                CompletionRequest request = builder.build();
                CompletionResponse response = client.completions(request);
                System.out.println(response);

                response.choices().forEach(e -> builder.message(e.message().ofAssistantMessage()));

                builder.message(ChatMessage.builder()
                        .content("А почему так? Будь еще более точным в формулировках")
                        .role(ChatMessageRole.USER).build());
            }
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }


}
