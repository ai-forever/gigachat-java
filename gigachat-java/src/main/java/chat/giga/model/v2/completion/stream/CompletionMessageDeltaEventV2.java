package chat.giga.model.v2.completion.stream;

import chat.giga.model.v2.completion.ChatMessageV2;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

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
     * Дельта сообщения; в некоторых payload встречается под ключом {@code message}.
     */
    @JsonProperty("delta")
    @JsonAlias("message")
    ChatMessageV2 delta;
}
