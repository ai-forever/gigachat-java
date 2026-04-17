package chat.giga.model.v2.completion;

import chat.giga.jackson.FlexibleLongDeserializer;
import chat.giga.model.v2.completion.stream.CompletionStreamUsageV2;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Тело ответа {@code v2/chat/completions} (несинхронный JSON).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponseV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Наименование модели.
     */
    @JsonProperty
    String model;

    /**
     * Идентификатор треда при непустом {@code storage} в запросе.
     */
    @JsonProperty("thread_id")
    String threadId;

    /**
     * Дата/время создания ответа (unix time).
     */
    @JsonProperty("created_at")
    @JsonDeserialize(using = FlexibleLongDeserializer.class)
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
     * Информация о потреблении токенов ({@code input_tokens}, {@code output_tokens}, {@code input_tokens_details} и
     * т.д. — см. «Состав ответа» в документации).
     */
    @JsonProperty
    CompletionStreamUsageV2 usage;

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

    /**
     * Состояние, фиксирующее работу с тулами ({@code tools_state_id} / алиас {@code tool_state_id}).
     */
    @JsonProperty("tools_state_id")
    String toolsStateId;
}
