package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.ResponseHandler;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.model.completion.CompletionChunkResponse;
import chat.giga.model.completion.CompletionRequest;

public class CompletionStreamingExample {

    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .clientId("test-client-id")
                .clientSecret("test-scope")
                .scope(Scope.GIGACHAT_API_PERS)
                .build();

        client.completions(CompletionRequest.builder()
                        .model(ModelName.GIGA_CHAT_MAX)
                        .message(ChatMessage.builder()
                                .content("Как дела")
                                .role(Role.USER)
                                .build())
                        .build(),
                new ResponseHandler<>() {
                    @Override
                    public void onNext(CompletionChunkResponse chunk) {
                        System.out.println("Chunk: " + chunk);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Stream completed");
                    }

                    @Override
                    public void onError(Throwable th) {
                        System.out.println("Error: " + th.getMessage());
                    }
                });
    }
}
