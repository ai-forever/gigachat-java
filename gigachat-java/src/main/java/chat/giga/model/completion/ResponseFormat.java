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
     * Если type = json_schema, то должно быть обязательно заполнено поле schema и опционально strict <br>
     * Если type = text, то дополнительных полей быть не должно.
     */
    @JsonProperty
    ResponseFormatType type;

    /**
     *  Если type = json_schema, то поле должно быть обязательно заполнено.
     * <p> Доп информация: <br>
     *  - Если блок required не указан в схеме → модель вернет json, но не обязательно с теми полями, что указаны в схеме. Единственное, что гарантировано - на выходе будет какой-то json <br>
     *  - Если блок required указан, но не указан strict → модель гарантированно вернет поля указанные в схеме, но допускается создание посторонних полей, не указанных в схеме <br>
     *  - Если указан и required и strict = true → никаких отклонений от схемы не будет
     */
    @JsonProperty
    Object schema;

    /**
     * Вывод жестко соответствует объявленной схеме.
     */
    @JsonProperty
    Boolean strict;
}
