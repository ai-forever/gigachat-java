package chat.giga.model.completion;

import chat.giga.jackson.FunctionCallJsonDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
@Accessors(fluent = true)
public class CompletionRequest {

    /**
     * Название модели.
     */
    @JsonProperty
    String model;

    /**
     * Список сообщений, которыми пользователь обменивался с моделью.
     */
    @JsonProperty
    @Singular
    List<ChatMessage> messages;

    /**
     * Поле, которое отвечает за то, как GigaChat будет работать с функциями. Может быть строкой или объектом. Возможные
     * значения:
     * <p>
     * none — режим работы по умолчанию. Если запрос не содержит function_call или значение поля — none, GigaChat не
     * вызовет функции, а просто сгенерирует ответ в соответствии с полученными сообщениями;
     * <p>
     * auto — в зависимости от содержимого запроса, модель решает сгенерировать сообщение или вызвать функцию. Модель
     * вызывает встроенные функции, если отсутствует массив functions с описанием пользовательских функций. Если запрос
     * содержит "function_call": "auto" и массив functions с описанием пользовательских функций, модель будет
     * генерировать аргументы для описанных функций и не сможет вызвать встроенные функции независимо от содержимого
     * запроса;
     * <p>
     * {"name": "название_функции"} — принудительная генерация аргументов для указанной функции. Вы можете явно задать
     * часть аргументов с помощью объекта partial_arguments. Остальные аргументы модель сгенерирует самостоятельно. При
     * принудительной генерации, массив functions обязан содержать объект с описанием указанной функции. В противном
     * случае вернется ошибка.
     */
    @JsonProperty("function_call")
    @JsonDeserialize(using = FunctionCallJsonDeserializer.class)
    @Default
    Object functionCall = ChatFunctionCallEnum.AUTO;

    /**
     * Список с описанием пользовательских функций.
     */
    @JsonProperty
    @Singular
    List<ChatFunction> functions;

    /**
     * Температура выборки. Чем выше значение, тем более случайным будет ответ модели. Если значение температуры
     * находится в диапазоне от 0 до 0.001, параметры `temperature` и `top_p` будут сброшены в режим, обеспечивающий
     * максимально детерминированный (стабильный) ответ модели. При значениях температуры больше двух, набор токенов в
     * ответе модели может отличаться избыточной случайностью.  Значение по умолчанию зависит от выбранной модели (поле
     * `model`) и может изменяться с обновлениями модели.
     */
    @JsonProperty
    Float temperature;

    /**
     * Параметр используется как альтернатива температуре (поле `temperature`). Задает вероятностную массу токенов,
     * которые должна учитывать модель. Так, если передать значение 0.1, модель будет учитывать только токены, чья
     * вероятностная масса входит в верхние 10%.  Значение по умолчанию зависит от выбранной модели (поле `model`) и
     * может изменяться с обновлениями модели. Значение изменяется в диапазоне от 0 до 1 включительно.
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
     * Количество повторений слов: Значение 1.0 — нейтральное значение. При значении больше 1 модель будет стараться не
     * повторять слова.  Значение по умолчанию зависит от выбранной модели (поле `model`) и может изменяться с
     * обновлениями модели.
     */
    @JsonProperty("repetition_penalty")
    Float repetitionPenalty;

    /**
     * Параметр потокового режима (`\"stream\": \"true\"`). Задает минимальный интервал в секундах, который проходит
     * между отправкой токенов. Например, если указать `1`, сообщения будут приходить каждую секунду, но размер каждого
     * из них будет больше, так как за секунду накапливается много токенов.
     */
    @JsonProperty("update_interval")
    @Default
    Integer updateInterval = 0;

    public static CompletionRequestBuilder builder() {
        return new CompletionRequestBuilder();
    }

    public static CompletionRequestBuilder builder(ChatFunctionCall functionCall) {
        return new CompletionRequestBuilder().functionCall(functionCall);
    }

    public static CompletionRequestBuilder builder(ChatFunctionCallEnum functionCall) {
        return new CompletionRequestBuilder().functionCall(functionCall);
    }
}
