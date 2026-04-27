package chat.giga.model.v2.completion.stream;

import chat.giga.jackson.FlexibleLongDeserializer;
import chat.giga.model.v2.completion.AdditionalDataV2;
import chat.giga.model.v2.completion.ChatMessageV2;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * Тело SSE-события {@link CompletionV2SseEvents#RESPONSE_MESSAGE_DONE} — явное окончание потока (в т.ч. при ошибке по
 * {@code finish_reason}).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionMessageDoneEventV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Наименование модели.
     */
    @JsonProperty
    String model;

    /**
     * Время создания (unix time; допускается строка в JSON — см. десериализатор).
     */
    @JsonProperty("created_at")
    @JsonDeserialize(using = FlexibleLongDeserializer.class)
    Long createdAt;

    /**
     * Итоговые сообщения ответа.
     */
    @JsonProperty
    @Singular("message")
    List<ChatMessageV2> messages;

    /**
     * Причина завершения генерации (в т.ч. ошибка); см. Finish reasons в документации.
     */
    @JsonProperty("finish_reason")
    String finishReason;

    /**
     * Учёт токенов для этого завершения потока.
     */
    @JsonProperty
    CompletionStreamUsageV2 usage;

    /**
     * Дополнительные данные ответа (поле {@code additional_data}; см. {@link AdditionalDataV2}).
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
     * Состояние тулов после ответа ({@code tools_state_id} / алиас {@code tool_state_id}).
     */
    @JsonProperty("tools_state_id")
    @JsonAlias("tool_state_id")
    String toolsStateId;
}
