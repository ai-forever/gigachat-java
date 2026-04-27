package chat.giga;

import chat.giga.client.CompletionV2StreamHandler;
import chat.giga.client.GigaChatClientAsync;
import chat.giga.client.GigaChatClientAsyncImpl;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.v2.completion.ChatMessageRoleV2;
import chat.giga.model.v2.completion.ChatMessageV2;
import chat.giga.model.v2.completion.CompletionRequestV2;
import chat.giga.model.v2.completion.MessageContentPartV2;
import chat.giga.model.v2.completion.ModelOptionsV2;
import chat.giga.model.v2.completion.ReasoningV2;
import chat.giga.model.v2.completion.stream.CompletionMessageDeltaEventV2;
import chat.giga.model.v2.completion.stream.CompletionMessageDoneEventV2;
import chat.giga.model.v2.completion.stream.CompletionToolLifecycleEventV2;

public class CompletionV2StreamingTest {

    public static void main(String[] args) {
        GigaChatClientAsyncImpl client = GigaChatClientAsync.builder()
                .readTimeout(120)
                .connectTimeout(120)
                .apiUrl("https://gigachat.sberdevices.ru/v1")
                .apiV2Url("https://gigachat.sberdevices.ru/v2")
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

        try {
            client.completionsV2Stream(CompletionRequestV2.builder()
                    .modelOptions(ModelOptionsV2.builder()
                            .reasoning(ReasoningV2.builder()
                                    .effort("medium").build())
                            .updateInterval(1)
                            .build())
                    .model(ModelName.GIGA_CHAT_REASONING_2)
                    .message(ChatMessageV2.builder()
                            .role(ChatMessageRoleV2.USER)
                            .contentPart(MessageContentPartV2.builder()
                                    .text(" Как доказать теорему ферма?")
                                    .build()
                            ).build())
                    .build(), new CompletionV2StreamHandler() {
                @Override
                public void onMessageDelta(CompletionMessageDeltaEventV2 event) {
                    System.out.println("delta");
                    System.out.println(event);
                }

                @Override
                public void onMessageDone(CompletionMessageDoneEventV2 event) {
                    System.out.println("done");
                    System.out.println(event);

                }

                @Override
                public void onToolInProgress(CompletionToolLifecycleEventV2 event) {
                    System.out.println("onToolInProgress");
                    CompletionV2StreamHandler.super.onToolInProgress(event);
                }

                @Override
                public void onToolCompleted(CompletionToolLifecycleEventV2 event) {
                    System.out.println("onToolCompleted");
                    CompletionV2StreamHandler.super.onToolCompleted(event);
                }

                @Override
                public void onComplete() {
                    System.out.println("completed");
                }

                @Override
                public void onError(Throwable th) {
                    System.out.println("error " + th.getMessage());
                }
            });

            Thread.sleep(120_000);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}

