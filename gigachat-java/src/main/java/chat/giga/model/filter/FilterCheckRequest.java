package chat.giga.model.filter;

import chat.giga.model.ModelName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Builder(toBuilder = true)
@Jacksonized
@Accessors(fluent = true)
public class FilterCheckRequest implements Serializable {

    /**
     * Версия класса для сериализации. Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Входной текст для проверки.
     */
    @JsonProperty
    String input;

    /**
     * Название модели для проверки.
     */
    @JsonProperty
    @Default
    String model = ModelName.GIGA_FILTER_DETECTION;
}
