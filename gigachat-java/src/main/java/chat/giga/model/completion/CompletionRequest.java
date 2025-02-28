package chat.giga.model.completion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class CompletionRequest {

    /**
     * Название модели.
     */
    @JsonProperty
    String model;

    /**
     * Массив сообщений, которыми пользователь обменивался с моделью.
     */
    @JsonProperty
    @Singular
    List<ChatMessage> messages;

    @JsonProperty("function_call")
    ChatFunctionCall functionCall;

    /**
     * Массив с описанием пользовательских функций.
     */
    @JsonProperty
    @Singular
    List<ChatFunction> functions;

    /**
     * Температура выборки. Чем выше значение, тем более случайным будет ответ модели. Если значение температуры
     * находится в диапазоне от 0 до 0.001, параметры `temperature` и `top_p` будут сброшены в режим, обеспечивающий
     * максимально детерменированный (стабильный) ответ модели. При значениях температуры больше двух, набор токенов в
     * ответе модели может отличаться избыточной случайностью.  Значение по умолчанию зависит от выбранной модели (поле
     * `model`) и может изменяться с обновлениями модели.
     */
    @JsonProperty
    Float temperature;

    /**
     * Параметр используется как альтернатива температуре (поле `temperature`). Задает вероятностную массу токенов,
     * которые должна учитывать модель. Так, если передать значение 0.1, модель будет учитывать только токены, чья
     * вероятностная масса входит в верхние 10%.  Значение по умолчанию зависит от выбранной модели (поле `model`) и
     * может изменяться с обновлениями модели.  Значение изменяется в диапазоне от 0 до 1 включительно.
     */
    @JsonProperty("top_p")
    Float topP;

    /**
     * Указывает что сообщения надо передавать по частям в потоке.  Сообщения передаются по протоколу SSE
     */
    @JsonProperty
    @Default
    Boolean stream = false;

    /**
     * Максимальное количество токенов, которые будут использованы для создания ответов.
     */
    @JsonProperty("max_tokens")
    Integer maxTokens;

    /**
     * Количество повторений слов:  * Значение 1.0 — нейтральное значение. * При значении больше 1 модель будет
     * стараться не повторять слова.  Значение по умолчанию зависит от выбранной модели (поле `model`) и может
     * изменяться с обновлениями модели.
     */
    @JsonProperty("repetition_penalty")
    Float repetitionPenalty;

    /**
     * Параметр потокового режима (`\"stream\": \"true\"`). Задает минимальный интервал в секундах, который проходит
     * между отправкой токенов. Например, если указать `1`, сообщения будут приходить каждую секунду, но размер каждого
     * из них будет больше, так как за секунду накапливается много токенов.
     */
    @JsonProperty("update_interval")
    Integer updateInterval;
}
