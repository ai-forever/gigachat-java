package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ResponseFormat implements Serializable {
    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Если type = json_schema, то Должно быть обязательно заполнено поле schema и опционально strict Если type = text,
     * то дополнительных полей быть не должно.
     */
    @JsonProperty
    ResponseFormatType type;

    /**
     *  Если type = json_schema, то поле должно быть обязательно заполнено.
     */
    @JsonProperty
    Object schema;

    /**
     * Вывод жестко соответствует объявленной схеме.
     */
    @JsonProperty
    Boolean strict;
}
