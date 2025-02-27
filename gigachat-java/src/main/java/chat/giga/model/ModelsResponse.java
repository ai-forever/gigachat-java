package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelsResponse {

    /**
     * Массив объектов с данными доступных моделей
     */
    private List<Model> data;

    /**
     * Тип сущности в ответе, например, список.
     */
    private String object;
}
