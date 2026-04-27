package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.v2.completion.ChatMessageRoleV2;
import chat.giga.model.v2.completion.ChatMessageV2;
import chat.giga.model.v2.completion.CompletionRequestV2;
import chat.giga.model.v2.completion.FunctionSpecificationV2;
import chat.giga.model.v2.completion.FunctionsToolPayloadV2;
import chat.giga.model.v2.completion.ToolConfigV2;
import chat.giga.model.v2.completion.ToolV2;
import chat.giga.util.JsonUtils;

/**
 * Пример запроса к {@code POST /v2/chat/completions} с пользовательским тулом {@code functions}.
 */
public class CompletionV2Example {

    public static void main(String[] args) throws Exception {
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId("test-client-id")
                                .clientSecret("test-scope")
                                .build())
                        .build())
                .build();

        var om = JsonUtils.objectMapper();
        var parameters = om.readTree(
                "{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\"}},\"required\":[\"location\"]}");

        var request = CompletionRequestV2.builder()
                .model(ModelName.GIGA_CHAT_PRO_2)
                .message(ChatMessageV2.textMessage(ChatMessageRoleV2.USER, "Погода в Москве на завтра"))
                .tool(ToolV2.ofFunctions(FunctionsToolPayloadV2.builder()
                        .specification(FunctionSpecificationV2.builder()
                                .name("weather_forecast")
                                .description("Прогноз погоды")
                                .parameters(parameters)
                                .build())
                        .build()))
                .toolConfig(ToolConfigV2.autoMode())
                .build();

        try {
            var response = client.completionsV2(request);
            System.out.println(response);
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
