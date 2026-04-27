package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Объект {@code function_result} в части контента сообщения с ролью {@code tool}.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class FunctionResultContentV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Наименование функции.
     */
    @JsonProperty
    String name;

    /**
     * Результат выполнения функции: объект или строка (формат согласуется с бэкендом).
     */
    @JsonProperty
    JsonNode result;
}
