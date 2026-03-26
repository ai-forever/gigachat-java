package chat.giga.model.batch;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Метод обработки запросов в пакетной задаче.
 */
public enum BatchMethod {

    /**
     * Генерация текста (chat completions).
     */
    CHAT_COMPLETIONS("chat_completions"),

    /**
     * Построение эмбеддингов.
     */
    EMBEDDER("embedder");

    private final String value;

    BatchMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }
}
