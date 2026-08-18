package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.Map;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChoiceMessageFunctionCall implements Serializable {

    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Идентификатор вызова функции. Возвращается моделью, можно использовать для сопоставления результата выполнения
     * функции с конкретным вызовом.
     */
    @JsonProperty
    String id;

    /**
     * Название функции.
     */
    @JsonProperty
    String name;

    /**
     * Аргументы для вызова функции в виде пар ключ-значение.
     */
    @JsonProperty
    @Singular
    Map<String, Object> arguments;
}
