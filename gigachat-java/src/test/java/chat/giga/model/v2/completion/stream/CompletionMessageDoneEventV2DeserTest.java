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
                  "created_at": 167890456789,
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
                        "ts_start": 1725000000,
                        "ts_end": 1725000010,
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
        assertThat(step0.tsStart()).isEqualTo(1725000000L);
        assertThat(step0.tsEnd()).isEqualTo(1725000010L);
        assertThat(step0.step().functionsIn()).containsExactly("text2image");
        assertThat(step0.step().functionsOut()).containsExactly("text2image");
    }

    @Test
    void deserializesExecutionStepsFunctionCalls() throws Exception {
        String json = """
                {
                  "additional_data": {
                    "execution_steps": [
                      {
                        "step": {
                          "function_calls": [
                            {"id": "fc-1", "name": "get_weather", "arguments": {"city": "Moscow"}},
                            {"id": "fc-2", "name": "get_rate", "arguments": {"currency": "USD"}}
                          ]
                        }
                      }
                    ]
                  }
                }
                """;
        var ev = mapper.readValue(json, CompletionMessageDoneEventV2.class);
        var calls = ev.additionalData().executionSteps().get(0).step().functionCalls();
        assertThat(calls).hasSize(2);
        assertThat(calls.get(0).id()).isEqualTo("fc-1");
        assertThat(calls.get(0).name()).isEqualTo("get_weather");
        assertThat(calls.get(0).arguments()).containsEntry("city", "Moscow");
        assertThat(calls.get(1).name()).isEqualTo("get_rate");
    }
}
