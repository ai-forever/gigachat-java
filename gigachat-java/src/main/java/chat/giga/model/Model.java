package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class Model {

    /**
     * Название модели.
     */
    @JsonProperty
    String id;

    /**
     * Тип сущности в ответе, например, модель.
     */
    @JsonProperty
    String object;

    /**
     * Владелец модели
     */
    @JsonProperty("owned_by")
    String ownedBy;

    /**
     * Тип модели. Значение `chat` указывает, что модель используется для генерации.
     */
    @JsonProperty
    @Default
    String type = "chat";
}
