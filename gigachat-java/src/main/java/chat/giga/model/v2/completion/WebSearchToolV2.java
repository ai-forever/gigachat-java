package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Параметры встроенного тула {@code web_search}: тип поиска, индексы и флаги для поисковой платформы (SP).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class WebSearchToolV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Тип функции поиска: {@code actual_info_web_search}, {@code web_search} или {@code safe_search} (для
     * {@code safe_search} отдельный {@code get_datetime} не требуется).
     */
    @JsonProperty
    String type;

    /**
     * Индексы для поиска в SP.
     */
    @JsonProperty
    @Singular
    List<String> indexes;

    /**
     * Флаги для SP.
     */
    @JsonProperty
    @Singular
    List<String> flags;
}
