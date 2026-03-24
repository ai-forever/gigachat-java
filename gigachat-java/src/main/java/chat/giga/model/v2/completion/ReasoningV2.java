package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ReasoningV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Степень «усилия» reasoning: {@code low}, {@code medium} или {@code high} (на момент документации доступен в
     * основном {@code medium}).
     */
    @JsonProperty
    String effort;
}
