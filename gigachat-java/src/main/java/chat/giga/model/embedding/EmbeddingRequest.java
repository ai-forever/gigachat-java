package chat.giga.model.embedding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingRequest {

    /**
     * Название модели, которая будет использована для создания эмбеддинга.
     */
    @JsonProperty
    @Default
    String model = "Embeddings";

    /**
     * Строка или массив строк, которые будут использованы для генерации эмбеддинга.
     */
    @JsonProperty
    @Singular("addInput")
    List<String> input;
}
