package chat.giga.model.batch;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Статус обработки пакетной задачи.
 */
public enum BatchStatus {

    /**
     * Задача создана и ожидает обработки.
     */
    CREATED("created"),

    /**
     * Задача выполняется.
     */
    IN_PROGRESS("in_progress"),

    /**
     * Задача выполнена.
     */
    COMPLETED("completed");

    private final String value;

    BatchStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
