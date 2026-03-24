package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Объект {@code ranker_options}: ранжирование кандидатов тулов/функций перед передачей в модель.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class RankerOptionsV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Включение или выключение ранжирования тулов.
     */
    @JsonProperty
    Boolean enabled;

    /**
     * Сколько тулов/функций передать в модель после ранжирования; если не задано — значение по умолчанию из
     * конфигурации бэкенда.
     */
    @JsonProperty("top_n")
    Integer topN;

    /**
     * Алиас (идентификатор) эмбеддера для ранжирования.
     */
    @JsonProperty("embeddings_model")
    String embeddingsModel;
}
