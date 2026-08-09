package chat.giga.model.completion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChoiceMessageReasoningContentTest {

    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    @Test
    void response_readsReasoningContent() throws Exception {
        var json = """
                {"choices":[{"index":0,"finish_reason":"stop","message":{
                  "role":"assistant","content":"9",
                  "reasoning_content":"3 яблока и 6 груш, всего 9"}}],
                 "model":"GigaChat-3-Ultra",
                 "usage":{"prompt_tokens":30,"completion_tokens":80,"total_tokens":110}}
                """;

        var response = MAPPER.readValue(json, CompletionResponse.class);
        var message = response.choices().get(0).message();

        assertThat(message.content()).isEqualTo("9");
        assertThat(message.reasoningContent()).isEqualTo("3 яблока и 6 груш, всего 9");
    }

    @Test
    void response_withoutReasoningContentKeepsFieldNull() throws Exception {
        var json = """
                {"choices":[{"index":0,"finish_reason":"stop",
                  "message":{"role":"assistant","content":"Париж"}}],
                 "model":"GigaChat-2"}
                """;

        var message = MAPPER.readValue(json, CompletionResponse.class).choices().get(0).message();

        assertThat(message.content()).isEqualTo("Париж");
        assertThat(message.reasoningContent()).isNull();
    }
}
