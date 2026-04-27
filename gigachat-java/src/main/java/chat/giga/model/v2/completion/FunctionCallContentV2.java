package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.Map;

/**
 * Объект {@code function_call} в части контента: имя функции и аргументы.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
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
    @Singular
    Map<String, Object> arguments;
}
