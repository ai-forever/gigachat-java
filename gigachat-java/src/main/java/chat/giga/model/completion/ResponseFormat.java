package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ResponseFormat implements Serializable {
    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty
    ResponseFormatType type;

    @JsonProperty
    Object schema;

    @JsonProperty
    Boolean strict;
}
