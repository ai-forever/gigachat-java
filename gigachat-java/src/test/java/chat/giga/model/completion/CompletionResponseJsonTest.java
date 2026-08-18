package chat.giga.model.completion;

import chat.giga.util.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionResponseJsonTest {

    @Test
    void deserializesFunctionCallWithIdAndUnknownFields() throws Exception {
        String json = """
                {
                  "choices": [{
                    "message": {
                      "content": "",
                      "role": "assistant",
                      "function_call": {"id": "bec20958-490d-4280-8744-70410c1a9cb8", "name": "spawnAgent",
                          "arguments": {"arg0": "harnessExecutor"}, "extra_field": 1},
                      "functions_state_id": "01a0108f",
                      "reasoning_content": "thinking..."
                    },
                    "index": 0,
                    "finish_reason": "function_call"
                  }],
                  "created": 1786984204,
                  "model": "glm-5.2:latest",
                  "object": "chat.completions",
                  "usage": {"prompt_tokens": 1, "completion_tokens": 2, "total_tokens": 3,
                      "precached_prompt_tokens": 0, "future_field": true}
                }
                """;

        CompletionResponse response = JsonUtils.objectMapper().readValue(json, CompletionResponse.class);

        ChoiceMessageFunctionCall functionCall = response.choices().get(0).message().functionCall();
        assertThat(functionCall.id()).isEqualTo("bec20958-490d-4280-8744-70410c1a9cb8");
        assertThat(functionCall.name()).isEqualTo("spawnAgent");
        assertThat(functionCall.arguments()).containsEntry("arg0", "harnessExecutor");
    }

    @Test
    void deserializesChunkWithUnknownFields() throws Exception {
        String json = """
                {
                  "choices": [{
                    "delta": {
                      "role": "assistant",
                      "function_call": {"id": "call-1", "name": "get_weather", "arguments": {"city": "Msk"},
                          "new_stream_field": "x"}
                    },
                    "index": 0
                  }],
                  "created": 1,
                  "model": "GigaChat",
                  "object": "chat.completion.chunk",
                  "usage": {"prompt_tokens": 1, "completion_tokens": 1, "total_tokens": 2, "unknown_usage_field": 5}
                }
                """;

        CompletionChunkResponse response = JsonUtils.objectMapper().readValue(json, CompletionChunkResponse.class);

        ChoiceMessageFunctionCall functionCall = response.choices().get(0).delta().functionCall();
        assertThat(functionCall.id()).isEqualTo("call-1");
        assertThat(functionCall.name()).isEqualTo("get_weather");
    }
}
