package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Builder(toBuilder = true)
@Jacksonized
@Accessors(fluent = true)
public class AiCheckRequest implements Serializable {

    /**
     * Версия класса для сериализации.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Название модели.
     */
    @JsonProperty
    String model;

    /**
     * Текст для проверки.
     */
    @JsonProperty
    String input;
}
