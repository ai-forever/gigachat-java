package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
 * Сообщение в массиве {@code messages} запроса/ответа v2: роль, опционально состояние тулов и массив частей контента.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ChatMessageV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Источник сообщения: {@code user}, {@code system}, {@code assistant}, {@code tool}, {@code reasoning} и прочие
     * роли по спецификации API.
     */
    @JsonProperty
    String role;

    /**
     * Состояние, фиксирующее работу с тулами ({@code tools_state_id} в JSON; в ответах также встречается алиас
     * {@code tool_state_id}).
     */
    @JsonProperty("tools_state_id")
    @JsonAlias("tool_state_id")
    String toolsStateId;

    /**
     * Содержимое сообщения: массив объектов-частей ({@link MessageContentPartV2}).
     */
    @JsonProperty
    @Singular("contentPart")
    List<MessageContentPartV2> content;

    /**
     * Одна текстовая часть (удобно для простых user/system сообщений).
     */
    public static ChatMessageV2 textMessage(String role, String text) {
        return ChatMessageV2.builder()
                .role(role)
                .contentPart(MessageContentPartV2.builder().text(text).build())
                .build();
    }
}
