package chat.giga.model.v2.completion.stream;

import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionMessageDoneEventV2DeserTest {

    private final ObjectMapper mapper = JsonUtils.objectMapper();

    @Test
    void deserializesDocStyleErrorDone() throws Exception {
        String json = """
                {
                  "model": "GigaChat",
                  "created_at": "167890456789",
                  "finish_reason": "error",
                  "usage": {
                    "input_tokens": 0,
                    "input_tokens_details": {"prompt_tokens": 0, "cached_tokens": 0},
                    "output_tokens": 0,
                    "total_tokens": 0
                  }
                }
                """;
        var ev = mapper.readValue(json, CompletionMessageDoneEventV2.class);
        assertThat(ev.model()).isEqualTo("GigaChat");
        assertThat(ev.createdAt()).isEqualTo(167890456789L);
        assertThat(ev.finishReason()).isEqualTo("error");
        assertThat(ev.usage().inputTokens()).isZero();
        assertThat(ev.usage().inputTokensDetails().cachedTokens()).isZero();
        assertThat(ev.usage().inputTokensDetails().promptTokens()).isZero();
    }

    @Test
    void deserializesAdditionalDataExecutionSteps() throws Exception {
        String json = """
                {
                  "finish_reason": "stop",
                  "additional_data": {
                    "execution_steps": [
                      {
                        "ts_start": "",
                        "ts_end": "",
                        "event_type": "execute_ranker",
                        "step": {
                          "functions_in": ["text2image"],
                          "functions_out": ["text2image"]
                        }
                      }
                    ]
                  }
                }
                """;
        var ev = mapper.readValue(json, CompletionMessageDoneEventV2.class);
        assertThat(ev.additionalData().executionSteps()).hasSize(1);
        var step0 = ev.additionalData().executionSteps().get(0);
        assertThat(step0.eventType()).isEqualTo("execute_ranker");
        assertThat(step0.step().functionsIn()).containsExactly("text2image");
        assertThat(step0.step().functionsOut()).containsExactly("text2image");
    }
}
