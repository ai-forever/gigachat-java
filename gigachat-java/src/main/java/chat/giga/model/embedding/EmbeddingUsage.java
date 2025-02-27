package chat.giga.model.embedding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class EmbeddingUsage {

    /**
     * Количество токенов в строке, для которой сгенерирован эмбеддинг.
     */
    @JsonProperty("prompt_tokens")
    Integer promptTokens;
}
