package chat.giga.model.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class FileDeletedResponse {

    /**
     * Идентификатор файла
     */
    @JsonProperty
    UUID id;

    /**
     * Признак удаления файла
     */
    @JsonProperty
    Boolean deleted;
}
