package chat.giga.model.v2.completion;

import chat.giga.model.completion.Usage;
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
 * Тело ответа {@code v2/chat/completions} (несинхронный JSON). Состав полей — таблица «Состав ответа» в документации
 * релиза WMapi.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CompletionResponseV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Наименование модели.
     */
    @JsonProperty
    String model;

    /**
     * Дата/время создания ответа (unix time).
     */
    @JsonProperty("created_at")
    Long createdAt;

    /**
     * Массив сообщений (ролей и контента) в ответе.
     */
    @JsonProperty
    @Singular
    List<ChatMessageV2> messages;

    /**
     * Причина завершения гипотезы генерации; подробный перечень значений см. в документации (Finish reasons), в т.ч.
     * для {@code function_call} и ошибок.
     */
    @JsonProperty("finish_reason")
    String finishReason;

    /**
     * Информация о потреблении токенов.
     */
    @JsonProperty
    Usage usage;

    /**
     * Дополнительные данные ответа: {@code execution_steps} и вложенная структура по документации релиза / proto
     * {@code AdditionalData}.
     */
    @JsonProperty("additional_data")
    AdditionalDataV2 additionalData;

    /**
     * Тип объекта ответа API.
     */
    @JsonProperty
    String object;

    /**
     * Идентификатор ответа.
     */
    @JsonProperty
    String id;
}
