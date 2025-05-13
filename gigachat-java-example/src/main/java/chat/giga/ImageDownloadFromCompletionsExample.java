package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatFunctionCallEnum;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.ChatMessageRole;

import java.util.Arrays;
import java.util.List;

public class ImageDownloadFromCompletionsExample {

    public static void main(String[] args) {
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId("test-client-id")
                                .clientSecret("test-scope")
                                .build())
                        .build())
                .build();
        try {
            // получаем список моделей
            var modelResponse = client.models();
            if (modelResponse != null) {
                var completionsResponse = client.completions(CompletionRequest.builder()
                        .model(modelResponse.data().get(0).id())
                        .messages(List.of(
                                ChatMessage.builder()
                                        .role(ChatMessageRole.SYSTEM)
                                        .content("Ты — художник Густав Климт")
                                        .build(),
                                ChatMessage.builder()
                                        .role(ChatMessageRole.USER)
                                        .content("Нарисуй розового кота")
                                        .build()))
                                .functionCall(ChatFunctionCallEnum.AUTO)
                        .build());
                if (completionsResponse != null) {
                    // получаем ответ модели на сообщения
                    String content = completionsResponse.choices().get(0).message().content();
                    if (content != null && content.contains("img src=")) {
                        var fileId = content.split("\"")[1];
                        // получаем информацию по сгенерированному файлу
                        System.out.println(client.fileInfo(fileId));
                        // скачиваем сгенерированный файл
                        System.out.println(Arrays.toString(client.downloadFile(fileId, null)));
                        // удаляем сгенерированный файл
                        System.out.println(client.deleteFile(fileId));
                    }
                }
            }
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
