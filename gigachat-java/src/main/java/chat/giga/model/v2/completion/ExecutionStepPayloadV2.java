package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
 * Объект {@code step} внутри элемента {@link ExecutionStepV2}: детали шага исполнения (вызов модели, ранжирование,
 * backend-функция и т.д.) по спецификации {@code gigachatv2.proto} / примерам {@code additional_data} в документации.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ExecutionStepPayloadV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("function_call")
    FunctionCallContentV2 functionCall;

    @JsonProperty("functions_in")
    @Singular("functionIn")
    List<String> functionsIn;

    @JsonProperty("functions_out")
    @Singular("functionOut")
    List<String> functionsOut;

    @JsonProperty("function_executed")
    String functionExecuted;

    @JsonProperty("function_result")
    String functionResult;
}
