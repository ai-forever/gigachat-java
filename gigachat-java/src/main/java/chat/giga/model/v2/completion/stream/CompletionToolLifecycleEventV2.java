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
 * Тело SSE-событий {@link CompletionV2SseEvents#RESPONSE_TOOL_IN_PROGRESS} и
 * {@link CompletionV2SseEvents#RESPONSE_TOOL_COMPLETED}.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionToolLifecycleEventV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Срез сообщений с обновлённым контентом (например {@code tool_execution}, reasoning).
     */
    @JsonProperty
    @Singular("message")
    List<ChatMessageV2> messages;
}
