package chat.giga.model.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class AvailableFilesResponse {

    /**
     * массив объектов с данными доступных файлов
     */
    @JsonProperty
    List<FileResponse> data;

}
