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
public class FilterCheckUsage implements Serializable {

    /**
     * Версия класса для сериализации. Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Количество токенов, использованных при фильтрации.
     */
    @JsonProperty("filter_tokens")
    Integer filterTokens;
}
