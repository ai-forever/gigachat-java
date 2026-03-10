package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.Scope;
import chat.giga.model.batch.BatchCreateResponse;
import chat.giga.model.batch.BatchItem;
import chat.giga.model.batch.BatchMethod;
import chat.giga.model.batch.BatchRequest;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class BatchExample {

    public static void main(String[] args) throws IOException {
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
            ObjectMapper objectMapper = JsonUtils.objectMapper();

            BatchCreateResponse createResponse = client.createBatch(List.of(
                    BatchRequest.completion("task-1", CompletionRequest.builder()
                            .model("GigaChat-3-Ultra")
                            .message(ChatMessage.builder()
                                    .role(ChatMessageRole.USER)
                                    .content("Какие факторы влияют на стоимость страховки на дом?")
                                    .build())
                            .build()),
                    BatchRequest.completion("task-2", CompletionRequest.builder()
                            .model("GigaChat-3-Ultra")
                            .message(ChatMessage.builder()
                                    .role(ChatMessageRole.USER)
                                    .content("Кратко перечисли три вида страхования.")
                                    .build())
                            .build())
            ), BatchMethod.CHAT_COMPLETIONS);
            String batchId = createResponse.id();
            System.out.println("Создан пакет: " + batchId);

            List<BatchItem> statusResponse = client.batchStatus(batchId);
            System.out.println("Статус пакета: " + statusResponse);

            byte[] resultBytes = client.downloadFile(statusResponse.get(0).outputFileId(), null);
            String resultJsonl = new String(resultBytes, StandardCharsets.UTF_8);

            // Парсим JSONL-результат: каждая строка — id подзадачи + CompletionResponse
            for (String line : resultJsonl.split("\n")) {
                if (line.isBlank()) {
                    continue;
                }
                JsonNode node = objectMapper.readTree(line);
                String taskId = node.path("id").asText();
                CompletionResponse completionResponse = objectMapper.treeToValue(
                        node.path("result"), CompletionResponse.class);
                System.out.println("id=" + taskId + " content=" +
                        completionResponse.choices().get(0).message().content());
            }
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
