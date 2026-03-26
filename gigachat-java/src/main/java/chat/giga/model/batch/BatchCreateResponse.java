package chat.giga.model.batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Ответ на создание пакетной задачи (POST /batches).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class BatchCreateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Идентификатор созданной пакетной задачи.
     */
    @JsonProperty("id")
    String id;

    /**
     * Метод обработки запросов.
     */
    @JsonProperty("method")
    BatchMethod method;

    /**
     * Количество запросов внутри пакетной задачи.
     */
    @JsonProperty("request_counts")
    RequestCounts requestCounts;

    /**
     * Статус обработки пакетной задачи.
     */
    @JsonProperty("status")
    BatchStatus status;

    /**
     * Время создания в формате unix timestamp.
     */
    @JsonProperty("created_at")
    long createdAt;

    /**
     * Время обновления в формате unix timestamp.
     */
    @JsonProperty("updated_at")
    long updatedAt;
}
