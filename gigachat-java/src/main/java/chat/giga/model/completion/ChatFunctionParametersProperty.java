package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChatFunctionParametersProperty implements Serializable {

    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

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
    Map<String, Object> items;

    /**
     * Возможные значения enum
     */
    @JsonProperty("enum")
    @Singular("addEnum")
    List<String> enums;

    /**
     * Описание параметров аргумента.
     */
    @JsonProperty
    @Singular
    Map<String, ChatFunctionParametersProperty> properties;
}
