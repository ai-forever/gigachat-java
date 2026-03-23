package chat.giga.model.filter;

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
public class FilterCheckResponse implements Serializable {

    /**
     * Версия класса для сериализации. Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Результат проверки текста.
     */
    @JsonProperty
    Category category;

    /**
     * Количество символов в тексте.
     */
    @JsonProperty
    Integer characters;

    /**
     * Количество токенов, использованных при проверке.
     */
    @JsonProperty
    Integer tokens;

    /**
     * Список интервалов, обозначающих фрагменты текста, написанные с использованием ИИ. Каждый интервал представляет
     * собой пару [start, end] с позициями символов.
     */
    @JsonProperty("ai_intervals")
    List<List<Integer>> aiIntervals;
}
