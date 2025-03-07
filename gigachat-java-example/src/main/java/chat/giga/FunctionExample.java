package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatFunction;
import chat.giga.model.completion.ChatFunctionCallEnum;
import chat.giga.model.completion.ChatFunctionFewShotExample;
import chat.giga.model.completion.ChatFunctionParameters;
import chat.giga.model.completion.ChatFunctionParametersProperty;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.model.completion.CompletionRequest;

import java.util.ArrayList;
import java.util.List;

public class FunctionExample {

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
            var messages = new ArrayList<ChatMessage>();
            messages.add(ChatMessage.builder()
                    .content("Погода в Москве на три дня")
                    .role(Role.USER)
                    .build());

            var function = ChatFunction.builder()
                    .name("weather_forecast")
                    .description("Возвращает температуру на заданный период")
                    .parameters(ChatFunctionParameters.builder()
                            .type("object")
                            .property("location", ChatFunctionParametersProperty.builder()
                                    .type("string")
                                    .description("Местоположение, например, название города")
                                    .build())
                            .property("format", ChatFunctionParametersProperty.builder()
                                    .type("string")
                                    .enums(List.of("celsius", "fahrenheit"))

                                    .description("Единицы измерения температуры")
                                    .build())
                            .property("num_days", ChatFunctionParametersProperty.builder()
                                    .type("integer")
                                    .description("Период, для которого нужно вернуть прогноз")
                                    .build())
                            .required(List.of("location", "num_days"))
                            .build())
                    .fewShotExample(ChatFunctionFewShotExample.builder()
                            .request("Какая погода в Москве в ближайшие три дня")
                            .param("location", "Moscow, Russia")
                            .param("format", "celsius")
                            .param("num_days", 3)
                            .build())
                    .returnParameters(ChatFunctionParameters.builder()
                            .type("object")
                            .property("location", ChatFunctionParametersProperty.builder()
                                    .type("string")
                                    .description("Местоположение, например, название города")
                                    .build())
                            .property("temperature", ChatFunctionParametersProperty.builder()
                                    .type("integer")
                                    .description("Температура для заданного местоположения")
                                    .build())
                            .property("forecast", ChatFunctionParametersProperty.builder()
                                    .type("array")
                                    .item("type", "string")
                                    .description("Описание погодных условий")
                                    .build())
                            .property("error", ChatFunctionParametersProperty.builder()
                                    .type("string")
                                    .description("Возвращается при возникновении ошибки. Содержит описание ошибки")
                                    .build())
                            .build())
                    .build();

            var response = client.completions(CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT_PRO)
                    .messages(messages)
                    .functionCall(ChatFunctionCallEnum.AUTO)
                    .function(function)
                    .build());

            System.out.println(response);
            var message = response.choices().get(0).message();
            messages.add(ChatMessage.of(message));
            messages.add(ChatMessage.builder()
                    .role(Role.FUNCTION)
                    .content("{\"temperature\": \"27\"}")
                    .name("weather_forecast")
                    .build());

            response = client.completions(CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT_PRO)
                    .messages(messages)
                    .functionCall(ChatFunctionCallEnum.AUTO)
                    .function(function)
                    .build());

            System.out.println(response);
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
