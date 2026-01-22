package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.SSL;
import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.ResponseFormat;
import chat.giga.model.completion.ResponseFormatType;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;


public class StructuredOutputExample {

    public static void main(String[] args) throws JsonProcessingException {

        GigaChatClient client = GigaChatClient.builder()
                .logRequests(true)
                .logResponses(true)
                .authClient(AuthClient.builder()
                        .withCertificatesAuth(new JdkHttpClientBuilder()
                                .ssl(SSL.builder()
                                        .truststorePassword(System.getenv("TRUST_PASSWORD"))
                                        .trustStoreType("PKCS12")
                                        .truststorePath(System.getenv("TRUST_PATH"))
                                        .keystorePassword(System.getenv("KEY_PASSWORD"))
                                        .keystoreType("PKCS12")
                                        .keystorePath(System.getenv("KEY_PATH"))
                                        .build())
                                .build())
                        .build())
                .apiUrl("https://gigachat-ift.sberdevices.delta.sbrf.ru/v1")
                .build();
        try {
            JsonNode schema = JsonUtils.objectMapper().readTree("""
                    {
                      "type": "object",
                      "properties": {
                        "date": {
                          "type": "string",
                          "description": "Дата в формате dd.mm.yy"
                        },
                        "event": {
                          "type": "string",
                          "description": "Наименование события"
                        }
                      },
                      "required": [
                        "date"
                      ]
                    }
                    """);

            System.out.println(client.completions(CompletionRequest.builder()
                    .responseFormat(ResponseFormat.builder()
                            .schema(schema)
                            .strict(true)
                            .type(ResponseFormatType.JSON_SCHEMA).build())
                    .model(ModelName.GIGA_CHAT_MAX_2 + "-preview")
                    .message(ChatMessage.builder()
                            .content("19 мая 2009  вышел в прокат фильм терминатор 4")
                            .role(ChatMessageRole.USER)
                            .build())
                    .build()));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
