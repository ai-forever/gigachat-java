package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Поле {@code additional_data} ответа {@code v2/chat/completions} и события {@code response.message.done}: по доке —
 * объект с массивом {@code execution_steps} (см. {@link ExecutionStepV2}).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalDataV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("execution_steps")
    @Singular("executionStep")
    List<ExecutionStepV2> executionSteps;
}
