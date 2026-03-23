package chat.giga.model.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

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
     * Признак наличия ненормативной лексики или других нежелательных элементов.
     */
    @JsonProperty("is_profane")
    Boolean isProfane;

    /**
     * Информация об использовании токенов при фильтрации.
     */
    @JsonProperty
    FilterCheckUsage usage;
}
