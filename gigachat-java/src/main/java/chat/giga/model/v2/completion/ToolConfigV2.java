package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Объект {@code tool_config}: поведение при вызове тулов. В JSON поля в snake_case ({@code tool_name},
 * {@code function_name}).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ToolConfigV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Режим вызова: {@code auto}, {@code none} или {@code forced}. В режиме {@code forced} выполняется принудительный
     * вызов встроенного тула или функции из {@code tools.functions}.
     */
    String mode;

    /**
     * Для встроенных тулов (например {@code image_generate}) при {@code mode = forced}.
     */
    String toolName;

    /**
     * Имя функции из {@code tools.functions} при {@code mode = forced}.
     */
    String functionName;

    public static ToolConfigV2 autoMode() {
        return ToolConfigV2.builder().mode("auto").build();
    }

    public static ToolConfigV2 noneMode() {
        return ToolConfigV2.builder().mode("none").build();
    }

    public static ToolConfigV2 forcedFunction(String functionName) {
        return ToolConfigV2.builder().mode("forced").functionName(functionName).build();
    }

    public static ToolConfigV2 forcedTool(String toolName) {
        return ToolConfigV2.builder().mode("forced").toolName(toolName).build();
    }
}
