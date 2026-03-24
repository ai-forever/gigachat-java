package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Элемент {@code specifications[]}: схема одной клиентской функции для tool-calling.
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FunctionSpecificationV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Имя функции. Рекомендуемое соглашение: {@code инструмент-имя_функции} для группировки по инструменту (например
     * {@code microsoftoutlook-send_mail}).
     */
    @JsonProperty
    String name;

    /**
     * Описание функции; допускается кириллица.
     */
    @JsonProperty
    String description;

    /**
     * Параметры вызова в виде JSON Schema.
     */
    @JsonProperty
    JsonNode parameters;

    /**
     * Примеры пар «запрос пользователя — параметры вызова» для улучшения работы модели на инференсе.
     */
    @JsonProperty("few_shot_examples")
    @Singular
    List<FewShotExampleV2> fewShotExamples;

    /**
     * JSON Schema возвращаемых функцией параметров.
     */
    @JsonProperty("return_parameters")
    JsonNode returnParameters;
}
