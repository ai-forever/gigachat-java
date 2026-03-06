package chat.giga.model.batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Количество запросов внутри пакетной задачи.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class RequestCounts implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Общее количество запросов.
     */
    @JsonProperty("total")
    int total;
}
