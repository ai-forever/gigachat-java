package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Вложенный объект {@code reasoning} в {@code model_options}: интенсивность режима reasoning.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ReasoningV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Степень «усилия» reasoning: {@code low}, {@code medium} или {@code high} или {@code off}.
     */
    @JsonProperty
    String effort;
}
