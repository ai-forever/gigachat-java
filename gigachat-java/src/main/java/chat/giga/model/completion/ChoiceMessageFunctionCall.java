package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChoiceMessageFunctionCall {

    /**
     * Название функции.
     */
    @JsonProperty
    String name;

    /**
     * Аргументы для вызова функции в виде пар ключ-значение.
     */
    @JsonProperty
    @Singular
    Map<String, Object> arguments;
}
