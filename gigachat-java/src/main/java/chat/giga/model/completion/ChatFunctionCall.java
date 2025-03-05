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
public class ChatFunctionCall {

    /**
     * Название функции.
     */
    @JsonProperty
    String name;

    /**
     * Набор в котором можно задать некоторые аргументы указанной функции. Остальные аргументы модель сгенерирует
     * самостоятельно.
     */
    @JsonProperty("partial_arguments")
    @Singular
    Map<String, Object> partialArguments;
}
