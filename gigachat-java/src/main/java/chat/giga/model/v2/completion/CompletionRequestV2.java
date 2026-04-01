package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Тело запроса {@code POST v2/chat/completions}: обращение к модели с учётом истории чата пользователя с ассистентом.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CompletionRequestV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Идентификатор модели, которую необходимо использовать. Обязательность зависит от сценария (при использовании
     * ассистента или уже созданного треда может не передаваться).
     */
    @JsonProperty
    String model;

    /**
     * Идентификатор ассистента. Для stateful: только в первом сообщении при создании треда; для stateless — в каждом
     * запросе, где нужен ассистент. Нельзя передавать одновременно с {@code model}; при передаче id треда не
     * передаётся.
     */
    @JsonProperty("assistant_id")
    String assistantId;

    /**
     * Идентификатор ячейки памяти (memory).
     */
    @JsonProperty("memory_id")
    String memoryId;

    /**
     * Массив сообщений чата (обязательно).
     */
    @JsonProperty
    @Singular
    List<ChatMessageV2> messages;

    /**
     * Потоковый режим ответа (SSE): поле верхнего уровня тела запроса по спецификации API v2.
     */
    @JsonProperty
    Boolean stream;

    /**
     * Настройки модели генерации.
     */
    @JsonProperty("model_options")
    ModelOptionsV2 modelOptions;

    /**
     * Использование ранжирования для тулов и функций: включение, число кандидатов после ранжирования, модель
     * эмбеддера.
     */
    @JsonProperty("ranker_options")
    RankerOptionsV2 rankerOptions;

    /**
     * Дополнительная информация о клиенте, передаваемая в модель и улучшающая качество ответов (например часовой пояс в
     * {@link UserInfoV2#timezone}).
     */
    @JsonProperty("user_info")
    UserInfoV2 userInfo;

    /**
     * Поведение при вызове тулов: режим {@code auto} / {@code none} / {@code forced} и при необходимости имя тула или
     * функции.
     */
    @JsonProperty("tool_config")
    ToolConfigV2 toolConfig;

    /**
     * Список используемых тулов; каждый элемент — ровно один тул (oneof).
     */
    @JsonProperty
    @Singular
    List<ToolV2> tools;
}
