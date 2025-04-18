package chat.giga;

import chat.giga.client.GigaChatClientAsync;
import chat.giga.client.ResponseHandler;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionChunkResponse;
import chat.giga.model.completion.CompletionRequest;

public class CompletionStreamingExample {

    public static void main(String[] args) {

        GigaChatClientAsync client = GigaChatClientAsync.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId("test-client-id")
                                .clientSecret("test-scope")
                                .build())
                        .build())
                .build();

        client.completions(CompletionRequest.builder()
                        .model(ModelName.GIGA_CHAT_MAX)
                        .message(ChatMessage.builder()
                                .content("Как дела")
                                .role(ChatMessageRole.USER)
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
