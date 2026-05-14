package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Настройки ранжирования функций.
 */
@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class FunctionRanker implements Serializable {

    /**
     * Версия класса для сериализации. Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Включение/выключение ранжирования тулов.
     */
    @JsonProperty
    Boolean enabled;

    /**
     * Количество тулов/функций, которые будут переданы в модель после ранжирования. Если параметр не передан, то
     * дефолтное значение кол-ва из конфига.
     */
    @JsonProperty("top_n")
    Integer topN;

}
