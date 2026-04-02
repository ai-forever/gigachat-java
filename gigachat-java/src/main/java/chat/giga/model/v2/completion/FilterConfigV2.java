package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Объект {@code filter_config} в теле {@code POST v2/chat/completions} (настройки фильтрации).
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterConfigV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("request_content")
    FilterRequestContentV2 requestContent;

    @JsonProperty("response_content")
    FilterResponseContentV2 responseContent;
}
