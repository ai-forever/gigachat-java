package chat.giga.model.batch;

import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Одна задача внутри пакетного запроса (одна строка JSONL).
 * <p>
 * Создаётся через фабричные методы:
 * <ul>
 *   <li>{@link #completion(String, CompletionRequest)} — для метода {@link BatchMethod#CHAT_COMPLETIONS}</li>
 *   <li>{@link #embedding(String, EmbeddingRequest)} — для метода {@link BatchMethod#EMBEDDER}</li>
 * </ul>
 */
public class BatchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор подзадачи внутри пакета.
     */
    @JsonProperty("id")
    private final String id;

    /**
     * Тело запроса: {@link CompletionRequest} или {@link EmbeddingRequest}.
     */
    @JsonProperty("request")
    private final Object request;

    private BatchRequest(String id, Object request) {
        this.id = id;
        this.request = request;
    }

    /**
     * Создаёт задачу для метода {@link BatchMethod#CHAT_COMPLETIONS}.
     *
     * @param id      уникальный идентификатор подзадачи
     * @param request тело запроса в формате POST /chat/completions
     */
    public static BatchRequest completion(String id, CompletionRequest request) {
        return new BatchRequest(id, request);
    }

    /**
     * Создаёт задачу для метода {@link BatchMethod#EMBEDDER}.
     *
     * @param id      уникальный идентификатор подзадачи
     * @param request тело запроса в формате POST /embeddings
     */
    public static BatchRequest embedding(String id, EmbeddingRequest request) {
        return new BatchRequest(id, request);
    }

    /**
     * Сериализует список задач в JSONL: каждая задача — отдельная строка JSON.
     *
     * @param requests список задач
     * @return байты JSONL-тела запроса
     */
    public static byte[] toJsonl(List<BatchRequest> requests) {
        var objectMapper = JsonUtils.objectMapper();
        var sb = new StringBuilder();
        for (BatchRequest request : requests) {
            try {
                sb.append(objectMapper.writeValueAsString(request)).append("\n");
            } catch (JsonProcessingException e) {
                throw new UncheckedIOException(e);
            }
        }
        return sb.toString().stripTrailing().getBytes(StandardCharsets.UTF_8);
    }

    public String id() {
        return id;
    }

    public Object request() {
        return request;
    }
}
