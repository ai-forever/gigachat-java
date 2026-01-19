package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChatFunction implements Serializable {

    /**
     * Версия класса для сериализации.
     * Изменить при несовместимых изменениях в структуре класса.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Название пользовательской функции, для которой будут сгенерированы аргументы.
     */
    @JsonProperty
    String name;

    /**
     * Текстовое описание функции.
     */
    @JsonProperty
    String description;

    /**
     * Набор пар ключ-значение, которые описывают аргументы функции.
     */
    @JsonProperty
    ChatFunctionParameters parameters;

    /**
     * Список с парами `запрос_пользователя`-`параметры_функции`, которые будут служить модели примерами ожидаемого
     * результата.
     */
    @JsonProperty("few_shot_examples")
    @Singular
    List<ChatFunctionFewShotExample> fewShotExamples;

    /**
     * Описание параметров, которые может вернуть ваша функция.
     */
    @JsonProperty("return_parameters")
    ChatFunctionParameters returnParameters;
}
