package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Model {

    /**
     * Название модели.
     */
    private String id;

    /**
     * Тип сущности в ответе, например, модель.
     */
    private String object;

    /**
     * Владелец модели
     */
    @JsonProperty("owned_by")
    private String ownedBy;

    /**
     * Тип модели. Значение `chat` указывает, что модель используется для генерации.
     */
    private String type;
}
