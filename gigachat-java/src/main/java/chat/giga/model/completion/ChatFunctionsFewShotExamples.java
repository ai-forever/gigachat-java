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
public class ChatFunctionsFewShotExamples {

    /**
     * Запрос пользователя.
     */
    @JsonProperty
    String request;

    /**
     * Пример заполнения параметров пользовательской функции.
     */
    @JsonProperty
    @Singular
    Map<String, Object> params;
}
