package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Элемент {@code tool_execution} в {@code content}: сведения о вызове платформенной функции (в потоковом режиме также
 * поля вроде {@code seconds_left} — при необходимости обрабатывайте через {@code @JsonIgnoreProperties} на вышестоящем
 * уровне).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolExecutionContentV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Имя платформенной функции (например {@code image_generation}).
     */
    @JsonProperty
    String name;

    /**
     * Статус выполнения: {@code success} или {@code fail}.
     */
    @JsonProperty
    String status;

    /**
     * Признак срабатывания фильтра при выполнении функции.
     */
    @JsonProperty
    Boolean censored;
}
