package chat.giga.model.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class FileDeletedResponse {

    /**
     * Идентификатор файла
     */
    @JsonProperty
    String id;

    /**
     * Признак удаления файла
     */
    @JsonProperty
    Boolean deleted;
}
