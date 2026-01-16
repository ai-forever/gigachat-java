package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class Usage implements Serializable {

    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

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
