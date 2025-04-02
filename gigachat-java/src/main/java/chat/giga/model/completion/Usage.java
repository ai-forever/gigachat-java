package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class Usage {

    /**
     * Количество токенов во входящем сообщении (роль `user`).
     */
    @JsonProperty("prompt_tokens")
    Integer promptTokens;

    /**
     * Количество токенов, сгенерированных моделью (роль `assistant`).
     */
    @JsonProperty("completion_tokens")
    Integer completionTokens;

    /**
     * Общее количество токенов.
     */
    @JsonProperty("total_tokens")
    Integer totalTokens;

    /**
     * Количество кэшированных токенов, которые не учитываются в расчете стоимости
     */
    @JsonProperty("precached_prompt_tokens")
    Integer precachedPromptTokens;

}
