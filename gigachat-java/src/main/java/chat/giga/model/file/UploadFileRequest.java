package chat.giga.model.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.File;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class UploadFileRequest {

    /**
     * Назначение загружаемого файла
     */
    @JsonProperty
    String purpose;

    /**
     * Загружаемый объект
     */
    @JsonProperty
    File file;
}
