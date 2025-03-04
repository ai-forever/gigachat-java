package chat.giga.model.file;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AccessPolicy {

    PRIVATE("private"),
    PUBLIC("public");

    private final String value;

    AccessPolicy(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
