package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Объект {@code tool_config}: поведение при вызове тулов. В JSON поля в snake_case ({@code tool_name},
 * {@code function_name}).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToolConfigV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Режим вызова: {@code auto}, {@code none}, {@code forced} или {@code any}. {@code forced} — принудительный вызов
     * встроенного тула или функции из {@code tools.functions}; {@code any} — модель гарантированно сгенерирует
     * аргументы минимум для одной из функций {@code functions_names_any}.
     */
    @JsonProperty
    String mode;

    /**
     * Для встроенных тулов (например {@code image_generate}) при {@code mode = forced}.
     */
    @JsonProperty("tool_name")
    String toolName;

    /**
     * Имя функции из {@code tools.functions} при {@code mode = forced}.
     */
    @JsonProperty("function_name")
    String functionName;

    /**
     * Список названий функций из {@code tools.functions.specifications} для режима {@code any}. В этом режиме модель
     * гарантированно сгенерирует аргументы минимум для одной из перечисленных функций.
     */
    @JsonProperty("functions_names_any")
    @Singular("functionsNamesAny")
    List<String> functionsNamesAny;

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

    public static ToolConfigV2 anyMode(String... functionNames) {
        return ToolConfigV2.builder()
                .mode("any")
                .functionsNamesAny(Arrays.asList(functionNames))
                .build();
    }

}
