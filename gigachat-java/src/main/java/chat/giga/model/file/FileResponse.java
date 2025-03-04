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
public class FileResponse {

    /**
     * Размер файла в байтах
     */
    @JsonProperty
    Integer bytes;

    /**
     * Время создания файла в формате unix timestamp.
     */
    @JsonProperty("created_at")
    Integer createdAt;

    /**
     * Название файла
     */
    @JsonProperty("filename")
    String fileName;

    /**
     * Идентификатор файла
     */
    @JsonProperty
    UUID id;

    /**
     * Тип объекта
     */
    @JsonProperty
    String object;

    /**
     * Назначение файлов.
     */
    @JsonProperty
    String purpose;

    /**
     * Доступность файла
     */
    @JsonProperty("access_policy")
    AccessPolicy accessPolicy;
}
