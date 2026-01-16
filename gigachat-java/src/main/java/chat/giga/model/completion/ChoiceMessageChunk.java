package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChoiceMessageChunk implements Serializable {

    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Роль автора сообщения.  Роль `function_in_progress` используется при работе встроенных функций в режиме потоковой
     * передачи токенов.
     */
    @JsonProperty
    ChatMessageRole role;

    /**
     * Содержимое сообщения, например, результат генерации.  В сообщениях с ролью `function_in_progress` содержит
     * информацию о том, сколько времени осталось до завершения работы встроенной функции.
     */
    @JsonProperty
    String content;

    /**
     * Идентификатор, который объединяет массив функций, переданных в запросе. Возвращается в ответе модели (сообщение с
     * `\"role\": \"assistant\"`) при вызове встроенных или собственных функций.
     */
    @JsonProperty("functions_state_id")
    String functionsStateId;


    @JsonProperty("function_call")
    ChoiceMessageFunctionCall functionCall;
}
