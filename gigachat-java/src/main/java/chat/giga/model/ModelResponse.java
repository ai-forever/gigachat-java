package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ModelResponse {

    /**
     * Список объектов с данными доступных моделей
     */
    @JsonProperty
    @Singular("addData")
    List<Model> data;

    /**
     * Тип сущности в ответе, например, список.
     */
    @JsonProperty
    String object;
}
