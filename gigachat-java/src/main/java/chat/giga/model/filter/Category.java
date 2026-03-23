package chat.giga.model.filter;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Результат проверки текста на содержание ненормативной лексики и других нежелательных элементов.
 */
public enum Category {
    /**
     * Текст сгенерирован с помощью нейросетевых моделей.
     */
    AI("ai"),
    /**
     * Текст написан человеком.
     */
    HUMAN("human"),
    /**
     * Текст содержит как фрагменты, сгенерированные с помощью моделей, так и написанные человеком.
     */
    MIXED("mixed");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public static Category fromValue(String value) {
        for (Category category : Category.values()) {
            if (category.value.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
