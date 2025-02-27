package chat.giga.model.completion;

import chat.giga.model.ModelName;
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
public class CompletionResponse {

    /**
     * Массив ответов модели.
     */
    @JsonProperty
    @Singular
    List<Choice> choices;

    /**
     * Дата и время создания ответа в формате unix timestamp.
     */
    @JsonProperty
    Integer created;

    /**
     * Название модели.
     */
    @JsonProperty
    ModelName model;

    @JsonProperty
    Usage usage;

    /**
     * Название вызываемого метода.
     */
    @JsonProperty
    String object;
}
