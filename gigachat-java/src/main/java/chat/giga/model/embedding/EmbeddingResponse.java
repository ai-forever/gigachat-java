package chat.giga.model.embedding;

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
public class EmbeddingResponse {

    /**
     * Формат структуры данных.
     */
    @JsonProperty
    @Default
    String object = "list";

    /**
     * Список с данными о векторном представлении текста.
     */
    @JsonProperty
    @Singular("addData")
    List<Embedding> data;

    /**
     * Название модели, которая используется для вычисления эмбеддинга.
     */
    @JsonProperty
    @Default
    String model = "Embeddings";
}
