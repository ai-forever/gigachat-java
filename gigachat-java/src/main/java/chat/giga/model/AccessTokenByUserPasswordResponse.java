package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
public class AccessTokenByUserPasswordResponse {
    /**
     * Токен для авторизации запросов.
     */
    @JsonProperty("tok")
    public String accessToken;

    /**
     * Дата и время истечения действия токена в миллисекундах, в формате unix timestamp.
     */
    @JsonProperty("exp")
    public long expiresAt;
}
