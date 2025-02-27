package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ModelName {

    GIGA_CHAT("GigaChat"), GIGA_CHAT_PRO("GigaChat-Pro"), GIGA_CHAT_MAX("GigaChat-Max");

    final String value;

    ModelName(String v) {
        value = v;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static ModelName fromValue(String value) {
        for (ModelName b : ModelName.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
