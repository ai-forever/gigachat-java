package chat.giga.model.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class TokenCount {

    /**
     * Описание того, какая информация содержится в объекте.
     */
    @JsonProperty
    @Default
    String object = "tokens";

    /**
     * Количество токенов в соответствующей строке.
     */
    @JsonProperty
    Integer tokens;

    /**
     * Количество символов в соответствующей строке.
     */
    @JsonProperty
    Integer characters;
}
