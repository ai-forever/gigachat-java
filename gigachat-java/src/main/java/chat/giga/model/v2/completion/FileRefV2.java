package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Элемент {@code files[]}: идентификатор предзагруженного файла. В ответе API к тому же объекту могут добавляться поля
 * вроде {@code target} и {@code mime}
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileRefV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Идентификатор предзагруженного файла (обязательно в запросе по спецификации).
     */
    @JsonProperty
    String id;

    /**
     * Назначение файла: {@code image}, {@code audio}, {@code cover}, {@code video}, {@code 3dmodel}.
     */
    @JsonProperty
    String target;

    /**
     * MIME-тип файла.
     */
    @JsonProperty
    String mime;
}
