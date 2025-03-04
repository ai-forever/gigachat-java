package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;

public class CompletionConversationExample {

    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .logRequests(true)
                .logResponses(true)
                .clientId("test-client-id")
                .clientSecret("test-scope")
                .scope(Scope.GIGACHAT_API_PERS)
                .build();

        CompletionRequest.CompletionRequestBuilder builder = CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT_PRO)
                .message(ChatMessage.builder()
                        .content("Отвечай как программист")
                        .role(Role.SYSTEM)
                        .build())
                .message(ChatMessage.builder()
                        .content("Как спроектировать идеальный SDK?")
                        .role(Role.USER).build());

        try {
            for (int i = 0; i < 4; i++) {
                CompletionRequest request = builder.build();
                CompletionResponse response = client.completions(request);
                System.out.println(response);

                response.choices().forEach(e -> builder.message(e.message().toAssistantMessage()));

                builder.message(ChatMessage.builder()
                        .content("А почему так? Будь еще более точным в формулировках")
                        .role(Role.USER).build());
            }
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }


}
