package chat.giga.model.v2.completion;

import chat.giga.model.completion.ResponseFormat;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Объект {@code model_options}: настройки модели генерации (температура, лимиты токенов и др.). Флаг стриминга задаётся
 * отдельным полем {@code stream} на верхнем уровне {@link CompletionRequestV2}.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ModelOptionsV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Пресет настроек (фильтры, модели, LoRA-адаптеры). Использование пресета должно быть согласовано с политикой
     * безопасности.
     */
    @JsonProperty
    String preset;

    /**
     * Температура выборки {@code > 0}: выше — более случайный вывод, ниже — более целенаправленный. Оптимальные
     * значения подбираются и подставляются по умолчанию для конкретной модели.
     */
    @JsonProperty
    Float temperature;

    /**
     * Ядерная выборка: учитываются токены с суммарной вероятностью до {@code top_p} (диапазон 0–1). Оптимальные
     * значения подбираются по умолчанию для модели.
     */
    @JsonProperty("top_p")
    Float topP;

    /**
     * Максимальное число токенов для генерации ответа (допустимо {@code > 0}).
     */
    @JsonProperty("max_tokens")
    Integer maxTokens;

    /**
     * Штраф за повторения: {@code 1.0} — без изменений; от 0 до 1 — поощрение уже сказанных слов; от 1 — избегание
     * повторов.
     */
    @JsonProperty("repetition_penalty")
    Float repetitionPenalty;

    /**
     * Интервал в секундах, не чаще которого в режиме стрима клиенту отправляются накопленные токены; {@code 0} —
     * отправлять по мере генерации.
     */
    @JsonProperty("update_interval")
    Float updateInterval;

    /**
     * Отключение нормализации истории (замена ролей и слияние подряд идущих сообщений user/assistant по правилам API).
     */
    @JsonProperty("unnormalized_history")
    Boolean unnormalizedHistory;

    /**
     * Число наиболее вероятных токенов и их логарифмических вероятностей на каждую позицию вывода (1–5).
     * Функциональность может быть закрыта пермишеном.
     */
    @JsonProperty("top_logprobs")
    Integer topLogprobs;

    /**
     * Настройки режима reasoning (например {@link ReasoningV2#effort}).
     */
    @JsonProperty
    ReasoningV2 reasoning;

    /**
     * Управление форматом ответа модели ({@code text} или {@code json_schema} и схема).
     */
    @JsonProperty("response_format")
    ResponseFormat responseFormat;
}
