package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChoiceMessageChunk {

    /**
     * Роль автора сообщения.  Роль `function_in_progress` используется при работе встроенных функций в режиме потоковой
     * передачи токенов.
     */
    @JsonProperty
    MessageRole role;

    /**
     * Содержимое сообщения, например, результат генерации.  В сообщениях с ролью `function_in_progress` содержит
     * информацию о том, сколько времени осталось до завершения работы встроенной функции.
     */
    @JsonProperty
    String content;


    @JsonProperty("function_call")
    ChoiceMessageFunctionCall functionCall;
}
