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
public class Embedding {

    /**
     * Тип объекта.
     */
    @JsonProperty
    @Default
    String object = "embedding";

    /**
     * Список чисел, представляющих значения эмбеддинга для предоставленного текста.
     */
    @JsonProperty
    @Singular("addEmbedding")
    List<Float> embedding;

    /**
     * Индекс соответствующий индексу текста, полученного в массиве `input` запроса.
     */
    @JsonProperty
    Integer index;

    /**
     * Количество токенов в строке, для которой сгенерирован эмбеддинг.
     */
    @JsonProperty
    EmbeddingUsage usage;
}
