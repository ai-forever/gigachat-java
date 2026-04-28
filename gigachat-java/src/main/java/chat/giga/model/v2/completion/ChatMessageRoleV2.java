package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Роль сообщения в API v2 ({@code messages} запроса и ответа, в т.ч. SSE).
 */
public enum ChatMessageRoleV2 {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    TOOL("tool"),
    REASONING("reasoning");

    private final String value;

    ChatMessageRoleV2(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ChatMessageRoleV2 fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ChatMessageRoleV2 r : values()) {
            if (r.value.equals(value)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unexpected message role '" + value + "'");
    }

    @JsonValue
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
