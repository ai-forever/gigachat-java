package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatMessageRole {

    SYSTEM("system"), USER("user"), ASSISTANT("assistant"), FUNCTION("function"), FUNCTION_IN_PROGRESS("function_in_progress");

    final String value;

    ChatMessageRole(String v) {
        value = v;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static ChatMessageRole fromValue(String value) {
        for (ChatMessageRole b : ChatMessageRole.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
