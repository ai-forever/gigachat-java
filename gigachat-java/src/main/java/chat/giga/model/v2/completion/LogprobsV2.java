package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

/**
 * Логарифмические вероятности сгенерированных токенов (поле {@code logprobs} в элементе {@code content} ответа).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogprobsV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Данные выбранного токена.
     */
    @JsonProperty
    LogprobTokenInfo chosen;

    /**
     * Массив с описанием наиболее вероятных токенов.
     */
    @JsonProperty
    @Singular("topToken")
    List<LogprobTokenInfo> top;

    /**
     * Данные одного токена (выбранного или из списка {@code top}).
     */
    @Value
    @Builder
    @Jacksonized
    @Accessors(fluent = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LogprobTokenInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @JsonProperty
        String token;

        @JsonProperty("token_id")
        Integer tokenId;

        @JsonProperty
        Float logprob;
    }
}
