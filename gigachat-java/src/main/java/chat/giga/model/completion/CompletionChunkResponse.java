package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class CompletionChunkResponse implements Serializable {

    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Массив ответов модели.
     */
    @JsonProperty
    @Singular
    List<ChoiceChunk> choices;

    /**
     * Дата и время создания ответа в формате unix timestamp.
     */
    @JsonProperty
    Integer created;

    /**
     * Название модели.
     */
    @JsonProperty
    String model;

    /**
     * Название вызываемого метода.
     */
    @JsonProperty
    String object;

    @JsonProperty
    Usage usage;
}
