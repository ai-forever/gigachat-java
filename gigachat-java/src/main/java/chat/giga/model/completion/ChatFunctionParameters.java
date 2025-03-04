package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChatFunctionParameters {

    /**
     * Тип параметров функции.
     */
    @JsonProperty
    String type;

    /**
     * Описание параметров функции.
     */
    @JsonProperty
    @Singular
    Map<String, ChatFunctionParametersProperty> properties;

    /**
     * Список обязательных параметров.
     */
    @JsonProperty
    List<String> required;

}
