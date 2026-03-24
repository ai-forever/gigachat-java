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
 * Один элемент {@code few_shot_examples}: обучающий пример для функции.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FewShotExampleV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Текст запроса пользователя.
     */
    @JsonProperty
    String request;

    /**
     * Ожидаемые параметры вызова в формате {@code "key": "value"}.
     */
    @JsonProperty
    JsonNode params;
}
