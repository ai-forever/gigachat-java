package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Объект {@code functions} в элементе {@code tools}: описание клиентских функций для следующего шага генерации.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class FunctionsToolPayloadV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Описания клиентских функций ({@code specifications} в JSON).
     */
    @JsonProperty("specifications")
    @Singular
    List<FunctionSpecificationV2> specifications;
}
