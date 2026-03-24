package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Один элемент массива {@code content} сообщения v2: в JSON обычно задано ровно одно из полей-веток ({@code text},
 * {@code files}, {@code function_call}, {@code function_result}, {@code tool_execution} и т.д.).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class MessageContentPartV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Текст сообщения.
     */
    @JsonProperty
    String text;

    /**
     * Информация о предзагруженном файле; может быть составной файл (например обложка + видео как результат
     * генерации).
     */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<FileRefV2> files;

    /**
     * Вызов функции в контексте ранее полученных сообщений с ролью {@code assistant}.
     */
    @JsonProperty("function_call")
    FunctionCallContentV2 functionCall;

    /**
     * Результат работы клиентской функции; заполняется при {@code role = "tool"}.
     */
    @JsonProperty("function_result")
    FunctionResultContentV2 functionResult;

    /**
     * Сведения о вызове платформенных (встроенных) функций, в т.ч. в потоковом режиме SSE.
     */
    @JsonProperty("tool_execution")
    ToolExecutionContentV2 toolExecution;

    /**
     * Дополнительные данные к другой структуре в той же части контента (например {@code sources} от поиска).
     */
    @JsonProperty("inline_data")
    Map<String, Object> inlineData;
}
