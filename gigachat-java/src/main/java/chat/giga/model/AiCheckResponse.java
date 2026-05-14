package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class AiCheckResponse implements Serializable {

    /**
     * Версия класса для сериализации.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Результат проверки: ai, human или mixed.
     */
    @JsonProperty
    String category;

    /**
     * Количество символов в переданном тексте.
     */
    @JsonProperty
    Integer characters;

    /**
     * Количество токенов в переданном тексте.
     */
    @JsonProperty
    Integer tokens;

    /**
     * Части текста, сгенерированные моделью. Каждый элемент — массив из двух целых чисел: [начало, конец].
     */
    @JsonProperty("ai_intervals")
    List<List<Integer>> aiIntervals;
}
