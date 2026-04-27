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
 * Один элемент {@code few_shot_examples}: обучающий пример для функции.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
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
    @Singular
    Map<String, Object> params;
}
