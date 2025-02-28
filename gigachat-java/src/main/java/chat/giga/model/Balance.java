package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class Balance {

    /**
     * Название модели, например, GigaChat или embeddings.
     */
    @JsonProperty
    String usage;

    /**
     * Остаток токенов
     */
    @JsonProperty
    Integer value;
}
