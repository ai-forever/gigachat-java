package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * Один элемент массива {@code tools}: в JSON задаётся ровно одна ветка (oneof) — клиентские функции или встроенный
 * тул.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ToolV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Список функций, доступных для вызова при следующей генерации ответа ({@code specifications} в JSON).
     */
    FunctionsToolPayloadV2 functions;

    /**
     * Встроенный тул генерации изображений (text2image).
     */
    @JsonProperty("image_generate")
    Map<String, Object> imageGenerate;

    /**
     * Веб-поиск: тип поиска, индексы и флаги для SP (см. {@link WebSearchToolV2}).
     */
    @JsonProperty("web_search")
    WebSearchToolV2 webSearch;

    /**
     * Встроенная функция извлечения содержимого по URL ({@code get_url_content}).
     */
    @JsonProperty("url_content_extraction")
    Map<String, Object> urlContentExtraction;

    /**
     * Встроенный тул даты/времени и актуального контекста (согласован с {@code user_info.timezone} в документации).
     */
    @JsonProperty("get_datetime")
    Map<String, Object> getDatetime;

    /**
     * Тул ячейки памяти; в простейшем случае передаётся как пустой объект {@code {}}.
     */
    Map<String, Object> memory;

    /**
     * Вызов code interpreter (в документации — advanced only).
     */
    @JsonProperty("code_interpreter")
    Map<String, Object> codeInterpreter;

    public static ToolV2 ofFunctions(FunctionsToolPayloadV2 functions) {
        return ToolV2.builder().functions(functions).build();
    }

    /**
     * Встроенный тул памяти {@code {"memory": {}}}.
     */
    public static ToolV2 memory() {
        return ToolV2.builder().memory(Collections.emptyMap()).build();
    }
}
