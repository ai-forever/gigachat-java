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
public class TokenCountRequest {

    /**
     * Название модели, которая будет использована для подсчета количества токенов.
     */
    @JsonProperty
    String model;

    /**
     * Список строк, в которых надо подсчитать количество токенов.
     */
    @JsonProperty
    @Singular("addInput")
    List<String> input;
}
