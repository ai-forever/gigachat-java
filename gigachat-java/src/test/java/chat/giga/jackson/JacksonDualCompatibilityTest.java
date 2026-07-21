package chat.giga.jackson;

import chat.giga.model.completion.ChatFunctionCall;
import chat.giga.model.completion.ChatFunctionCallEnum;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.v2.completion.ExecutionStepPayloadV2;
import chat.giga.model.v2.completion.FunctionResultContentV2;
import chat.giga.model.v2.completion.FunctionSpecificationV2;
import chat.giga.model.v2.completion.stream.CompletionMessageDoneEventV2;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonDualCompatibilityTest {

    private final ObjectMapper jackson2 = JsonUtils.objectMapper();
    private final JsonMapper jackson3 = JsonMapper.builder().build();

    @Test
    void completionRequestDeserializesOnJackson2AndJackson3() throws Exception {
        String json = """
                {
                  "model": "GigaChat",
                  "messages": [{"role": "user", "content": "hi"}],
                  "function_call": "auto"
                }
                """;

        CompletionRequest from2 = jackson2.readValue(json, CompletionRequest.class);
        CompletionRequest from3 = jackson3.readValue(json, CompletionRequest.class);

        assertThat(from2.model()).isEqualTo("GigaChat");
        assertThat(from2.functionCall()).isEqualTo(ChatFunctionCallEnum.AUTO);
        assertThat(from2.messages()).hasSize(1);

        assertThat(from3.model()).isEqualTo("GigaChat");
        assertThat(from3.functionCall()).isEqualTo(ChatFunctionCallEnum.AUTO);
        assertThat(from3.messages()).hasSize(1);
    }

    @Test
    void functionCallObjectDeserializesOnBoth() throws Exception {
        String json = """
                {
                  "model": "GigaChat",
                  "messages": [{"role": "user", "content": "hi"}],
                  "function_call": {"name": "get_weather", "partial_arguments": {"city": "Moscow"}}
                }
                """;

        CompletionRequest from2 = jackson2.readValue(json, CompletionRequest.class);
        CompletionRequest from3 = jackson3.readValue(json, CompletionRequest.class);

        assertThat(from2.functionCall()).isInstanceOf(ChatFunctionCall.class);
        assertThat(((ChatFunctionCall) from2.functionCall()).name()).isEqualTo("get_weather");
        assertThat(from3.functionCall()).isInstanceOf(ChatFunctionCall.class);
        assertThat(((ChatFunctionCall) from3.functionCall()).name()).isEqualTo("get_weather");
    }

    @Test
    void createdAtNumberOnJackson2AndJackson3() throws Exception {
        String json = """
                {"model":"GigaChat","created_at":167890456789,"finish_reason":"stop"}
                """;

        CompletionMessageDoneEventV2 from2 = jackson2.readValue(json, CompletionMessageDoneEventV2.class);
        CompletionMessageDoneEventV2 from3 = jackson3.readValue(json, CompletionMessageDoneEventV2.class);

        assertThat(from2.createdAt()).isEqualTo(167890456789L);
        assertThat(from3.createdAt()).isEqualTo(167890456789L);
    }

    @Test
    void neutralJsonFieldsRoundTripOnBoth() throws Exception {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of("city", Map.of("type", "string")));

        FunctionSpecificationV2 spec = FunctionSpecificationV2.builder()
                .name("get_weather")
                .description("weather")
                .parameters(schema)
                .returnParameters(Map.of("type", "object"))
                .build();

        FunctionResultContentV2 result = FunctionResultContentV2.builder()
                .name("get_weather")
                .result(Map.of("temp_c", 5))
                .build();

        ExecutionStepPayloadV2 step = ExecutionStepPayloadV2.builder()
                .functionExecuted("get_weather")
                .functionResult(List.of("ok"))
                .build();

        FunctionSpecificationV2 from2 = jackson2.readValue(jackson2.writeValueAsString(spec),
                FunctionSpecificationV2.class);
        FunctionSpecificationV2 from3 = jackson3.readValue(jackson3.writeValueAsString(spec),
                FunctionSpecificationV2.class);
        assertThat(from2.parameters()).isInstanceOf(Map.class);
        assertThat(from3.parameters()).isInstanceOf(Map.class);

        FunctionResultContentV2 result2 = jackson2.readValue(jackson2.writeValueAsString(result),
                FunctionResultContentV2.class);
        FunctionResultContentV2 result3 = jackson3.readValue(jackson3.writeValueAsString(result),
                FunctionResultContentV2.class);
        assertThat(result2.result()).isInstanceOf(Map.class);
        assertThat(result3.result()).isInstanceOf(Map.class);

        ExecutionStepPayloadV2 step2 = jackson2.readValue(jackson2.writeValueAsString(step),
                ExecutionStepPayloadV2.class);
        ExecutionStepPayloadV2 step3 = jackson3.readValue(jackson3.writeValueAsString(step),
                ExecutionStepPayloadV2.class);
        assertThat(step2.functionResult()).isInstanceOf(List.class);
        assertThat(step3.functionResult()).isInstanceOf(List.class);
    }

    @Test
    void jackson3CanConstructBuilderizedRequestWithoutCreatorsError() {
        CompletionRequest request = CompletionRequest.builder()
                .model("GigaChat")
                .message(ChatMessage.builder()
                        .role(ChatMessageRole.USER)
                        .content("ping")
                        .build())
                .functionCall(ChatFunctionCallEnum.NONE)
                .build();

        String json = jackson3.writeValueAsString(request);
        CompletionRequest roundTrip = jackson3.readValue(json, CompletionRequest.class);

        assertThat(roundTrip.model()).isEqualTo("GigaChat");
        assertThat(roundTrip.functionCall()).isEqualTo(ChatFunctionCallEnum.NONE);
        assertThat(roundTrip.messages()).hasSize(1);
    }
}
