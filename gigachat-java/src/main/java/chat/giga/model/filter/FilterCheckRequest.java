package chat.giga.model.filter;

import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatFunction;
import chat.giga.model.completion.ChatMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
@Accessors(fluent = true)
public class FilterCheckRequest implements Serializable {

    /**
     * Версия класса для сериализации. Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Название модели.
     */
    @JsonProperty
    @Default
    String model = ModelName.GIGA_FILTER_CLASSIFICATION;

    /**
     * Настройки фильтрации.
     */
    @JsonProperty
    FilterCheckSettings settings;

    /**
     * Список сообщений для проверки.
     */
    @JsonProperty
    @Singular
    List<ChatMessage> messages;

    /**
     * Список с описанием пользовательских функций.
     */
    @JsonProperty
    @Singular
    List<ChatFunction> functions;
}
