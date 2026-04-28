package chat.giga.model.v2.completion.stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Фрагмент {@code usage.input_tokens_details} в SSE {@code response.message.done} (см. документацию релиза).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InputTokensDetailsStreamV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Часть входных токенов, отнесённая к промпту (если передаётся отдельно от агрегата).
     */
    @JsonProperty("prompt_tokens")
    Integer promptTokens;

    /**
     * Кешированные токены ({@code cached_tokens} в документации ответа).
     */
    @JsonProperty("cached_tokens")
    Integer cachedTokens;
}
