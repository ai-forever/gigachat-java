package chat.giga.model.v2.completion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionV2RequestResponseJsonTest {

    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    @Test
    void request_serializesFilterFlagsStorage() throws Exception {
        var req = CompletionRequestV2.builder()
                .model("GigaChat")
                .message(ChatMessageV2.textMessage(ChatMessageRoleV2.USER, "hi"))
                .stream(false)
                .disableFilter(true)
                .filterConfig(FilterConfigV2.builder()
                        .requestContent(FilterRequestContentV2.builder()
                                .neuro(true)
                                .blacklist(false)
                                .whitelist(true)
                                .build())
                        .responseContent(FilterResponseContentV2.builder()
                                .blacklist(true)
                                .build())
                        .build())
                .flag("preprocess")
                .storage(CompletionStorageV2.builder()
                        .limit(10)
                        .threadId("t1")
                        .metadata(Map.of("k", "v"))
                        .build())
                .build();

        var tree = MAPPER.readTree(MAPPER.writeValueAsString(req));
        assertThat(tree.path("disable_filter").asBoolean()).isTrue();
        assertThat(tree.path("filter_config").path("request_content").path("neuro").asBoolean()).isTrue();
        assertThat(tree.path("flags").get(0).asText()).isEqualTo("preprocess");
        assertThat(tree.path("storage").path("limit").asInt()).isEqualTo(10);
        assertThat(tree.path("storage").path("thread_id").asText()).isEqualTo("t1");
    }

    @Test
    void response_deserializesDocUsageAndThreadId() throws Exception {
        String json = """
                {
                  "model": "m",
                  "thread_id": "thr-1",
                  "created_at": 1,
                  "messages": [{"message_id": "msg-1", "role": "assistant", "content": [{"text": "ok"}]}],
                  "finish_reason": "stop",
                  "usage": {
                    "input_tokens": 1,
                    "input_tokens_details": {"cached_tokens": 2, "prompt_tokens": 3},
                    "output_tokens": 4,
                    "total_tokens": 5
                  },
                  "object": "chat.completion",
                  "id": "id-1"
                }
                """;
        var res = MAPPER.readValue(json, CompletionResponseV2.class);
        assertThat(res.threadId()).isEqualTo("thr-1");
        assertThat(res.messages()).hasSize(1);
        assertThat(res.messages().get(0).messageId()).isEqualTo("msg-1");
        assertThat(res.messages().get(0).role()).isEqualTo(ChatMessageRoleV2.ASSISTANT);
        assertThat(res.usage().inputTokens()).isEqualTo(1);
        assertThat(res.usage().inputTokensDetails().cachedTokens()).isEqualTo(2);
        assertThat(res.usage().outputTokens()).isEqualTo(4);
    }
}
