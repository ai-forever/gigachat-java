package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class Choice {

    @JsonProperty
    ChoiceMessage message;

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
    FinishReason finishReason;

    public enum FinishReason {

        STOP("stop"), LENGTH("length"), FUNCTION_CALL("function_call"), BLACKLIST("blacklist"), ERROR("error");


        final String value;

        FinishReason(String v) {
            value = v;
        }

        @JsonValue
        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static FinishReason fromValue(String value) {
            for (FinishReason b : FinishReason.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
