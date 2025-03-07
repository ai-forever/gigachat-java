package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatFunctionCallEnum {
    AUTO("auto"), NONE("none");

    final String value;

    ChatFunctionCallEnum(String v) {
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
    public static ChatFunctionCallEnum fromValue(String value) {
        for (ChatFunctionCallEnum b : ChatFunctionCallEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
