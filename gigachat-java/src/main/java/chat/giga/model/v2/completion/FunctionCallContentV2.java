package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Объект {@code function_call} в части контента: имя функции и аргументы.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FunctionCallContentV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Наименование функции.
     */
    @JsonProperty
    String name;

    /**
     * Аргументы функции: объект «ключ — значение» по спецификации вызова.
     */
    @JsonProperty
    JsonNode arguments;
}
