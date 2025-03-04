package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChoiceFinishReason {
    STOP("stop"), LENGTH("length"), FUNCTION_CALL("function_call"), BLACKLIST("blacklist"), ERROR("error");


    final String value;

    ChoiceFinishReason(String v) {
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
    public static ChoiceFinishReason fromValue(String value) {
        for (ChoiceFinishReason b : ChoiceFinishReason.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
