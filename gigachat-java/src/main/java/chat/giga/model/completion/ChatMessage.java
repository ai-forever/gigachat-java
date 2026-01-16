package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChatMessage implements Serializable {

    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Роль автора сообщения: `system` — системный промпт, который задает роль модели, например, должна модель отвечать
     * как академик или как школьник; `assistant` — ответ модели; `user` — сообщение пользователя; `function` —
     * сообщение с результатом работы пользовательской функции.
     */
    @JsonProperty
    ChatMessageRole role;

    /**
     * Содержимое сообщения. Зависит от роли. Если поле передается в сообщении с ролью `function`, то в нем указывается
     * обернутый в строку валидный JSON-объект с аргументами функции, указанной в поле `function_call.name`.  В
     * остальных случаях содержит либо системный промпт (сообщение с ролью `system`), либо текст сообщения пользователя
     * или модели.
     */
    @JsonProperty
    String content;

    /**
     * Идентификатор, который объединяет массив функций, переданных в запросе. Возвращается в ответе модели (сообщение с
     * `\"role\": \"assistant\"`) при вызове встроенных или собственных функций.
     */
    @JsonProperty("functions_state_id")
    String functionsStateId;

    /**
     * Список идентификаторов файлов, которые нужно использовать при генерации. Идентификатор присваивается файлу при
     * загрузке в хранилище
     */
    @JsonProperty
    @Singular
    List<String> attachments;

    /**
     * Имя пользовательской функции из function_call.name
     */
    @JsonProperty
    String name;

    public static ChatMessage of(ChoiceMessage message) {
        return new ChatMessage(ChatMessageRole.fromValue(message.role().value()), message.content(), message.functionsStateId(),
                null, null);
    }
}
