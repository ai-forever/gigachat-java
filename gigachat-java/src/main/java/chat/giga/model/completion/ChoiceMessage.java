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
public class ChoiceMessage {

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

    /**
     * Передается в сообщениях с ролью`function_in_progress`. Содержит информацию о том, когда был создан фрагмент
     * сообщения.
     */
    @JsonProperty
    Integer created;

    /**
     * Название вызванной встроенной функции. Передается в сообщениях с ролью`function_in_progress`.
     */
    @JsonProperty
    String name;

    /**
     * Идентификатор, который объединяет массив функций, переданных в запросе. Возвращается в ответе модели (сообщение с
     * `\"role\": \"assistant\"`) при вызове встроенных или собственных функций.
     */
    @JsonProperty("functions_state_id")
    String functionsStateId;


    @JsonProperty("function_call")
    ChoiceMessageFunctionCall functionCall;

    public ChatMessage ofAssistantMessage() {
        return ChatMessage.builder()
                .content(content)
                .role(ChatMessage.Role.ASSISTANT).build();
    }
}
