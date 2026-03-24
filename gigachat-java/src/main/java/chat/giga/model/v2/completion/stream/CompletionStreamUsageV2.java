package chat.giga.model.v2.completion.stream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Поле {@code usage} в SSE {@code response.message.done} (в т.ч. вариант с {@code input_tokens} /
 * {@code input_tokens_details} из доки).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CompletionStreamUsageV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Токены входного запроса.
     */
    @JsonProperty("input_tokens")
    Integer inputTokens;

    /**
     * Токены сгенерированного ответа.
     */
    @JsonProperty("output_tokens")
    Integer outputTokens;

    /**
     * Суммарное число израсходованных токенов.
     */
    @JsonProperty("total_tokens")
    Integer totalTokens;

    /**
     * Альтернативное имя для входных токенов (совместимость с разными формами payload).
     */
    @JsonProperty("prompt_tokens")
    Integer promptTokens;

    /**
     * Альтернативное имя для выходных токенов.
     */
    @JsonProperty("completion_tokens")
    Integer completionTokens;

    /**
     * Предкешированные токены промпта (если присутствуют в ответе).
     */
    @JsonProperty("precached_prompt_tokens")
    Integer precachedPromptTokens;

    /**
     * Детализация входных токенов (в т.ч. кешированные).
     */
    @JsonProperty("input_tokens_details")
    InputTokensDetailsStreamV2 inputTokensDetails;
}
