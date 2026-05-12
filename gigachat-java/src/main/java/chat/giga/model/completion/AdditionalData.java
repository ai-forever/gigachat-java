package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Дополнительные данные для запроса.
 */
@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class AdditionalData implements Serializable {

    /**
     * Версия класса для сериализации. Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Features для дополнительной настройки.
     */
    @JsonProperty
    Features features;

    /**
     * Features для дополнительной настройки.
     */
    @Value
    @Builder(toBuilder = true)
    @Accessors(fluent = true)
    public static class Features implements Serializable {

        /**
         * Версия класса для сериализации.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Переопределение модели эмбеддера по алиасу для похода в ранкер.
         */
        @JsonProperty("embedder_model")
        String embedderModel;
    }
}
