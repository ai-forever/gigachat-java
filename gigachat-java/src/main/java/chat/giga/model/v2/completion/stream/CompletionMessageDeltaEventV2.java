package chat.giga.model.v2.completion.stream;

import chat.giga.model.v2.completion.ChatMessageV2;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Тело SSE-события {@link CompletionV2SseEvents#RESPONSE_MESSAGE_DELTA}.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionMessageDeltaEventV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Наименование модели.
     */
    @JsonProperty("model")
    String model;

    /**
     * Время создания (unix time).
     */
    @JsonProperty("created_at")
    Long createdAt;

    /**
     * Массив сообщений ответа.
     */
    @JsonProperty("messages")
    @Singular("message")
    List<ChatMessageV2> messages;
}
