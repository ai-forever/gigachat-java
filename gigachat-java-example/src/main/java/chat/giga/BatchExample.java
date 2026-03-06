package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.Scope;
import chat.giga.model.batch.BatchCreateResponse;
import chat.giga.model.batch.BatchItem;
import chat.giga.model.batch.BatchMethod;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class BatchExample {

    public static void main(String[] args) throws JsonProcessingException {
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
            // Формируем JSONL: каждая строка — объект с "id" и "request"
            // request — тот же формат, что и для POST /chat/completions
            String jsonl = """
                    {"id":"task-1","request":{"model":"GigaChat-3-Ultra","messages":[{"role":"user","content":"Какие факторы влияют на стоимость страховки на дом?"}]}}
                    {"id":"task-2","request":{"model":"GigaChat-3-Ultra","messages":[{"role":"user","content":"Кратко перечисли три вида страхования."}]}}
                    """;
            byte[] jsonlRequest = jsonl.stripTrailing().getBytes(StandardCharsets.UTF_8);

            BatchCreateResponse createResponse = client.createBatch(jsonlRequest, BatchMethod.CHAT_COMPLETIONS);
            String batchId = createResponse.id();
            System.out.println("Создан пакет: " + batchId);

            List<BatchItem> statusResponse = client.batchStatus(batchId);
            System.out.println("Статус пакета: " + statusResponse);

            byte[] resultBytes = client.downloadFile(statusResponse.get(0).outputFileId(), null);
            String resultJsonl = new String(resultBytes, StandardCharsets.UTF_8);
            System.out.println("result: " + resultJsonl);

            ObjectMapper objectMapper = JsonUtils.objectMapper();
            for (String line : resultJsonl.split("\n")) {
                if (line.isBlank()) {
                    continue;
                }
                JsonNode node = objectMapper.readTree(line);
                String taskId = node.path("id").asText();
                CompletionResponse completionResponse = objectMapper.treeToValue(
                        node.path("result"), CompletionResponse.class);
                System.out.println("id=" + taskId + " content=" +
                        completionResponse);
            }
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
