package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChatFunctionParametersProperty {

    /**
     * Тип аргумента функции
     */
    @JsonProperty
    String type;

    /**
     * Описание аргумента
     */
    @JsonProperty
    String description;

    /**
     * Возможные значения аргумента
     */
    @JsonProperty
    @Singular
    List<ChatFunctionParametersProperty> items;

    /**
     * Возможные значения enum
     */
    @JsonProperty("enum")
    @Singular("addEnum")
    List<String> enums;
}
