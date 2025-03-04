package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class ChoiceChunk {

    @JsonProperty
    ChoiceMessageChunk delta;

    /**
     * Индекс сообщения в массиве, начиная с ноля.
     */
    @JsonProperty
    Integer index;

    /**
     * Причина завершения гипотезы. Возможные значения: `stop` — модель закончила формировать гипотезу и вернула полный
     * ответ; `length` — достигнут лимит токенов в сообщении; `function_call` — указывает, что при запросе была вызвана
     * встроенная функция или сгенерированы аргументы для пользовательской функции; `blacklist` — запрос попадает под
     * тематические ограничения. `error` — ответ модели содержит невалидные аргументы пользовательской функции.
     */
    @JsonProperty("finish_reason")
    ChoiceFinishReason finishReason;
}
