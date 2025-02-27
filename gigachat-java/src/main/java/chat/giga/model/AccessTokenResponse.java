package chat.giga.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse {

    /**
     * Токен для авторизации запросов.
     */
    @JsonProperty("access_token")
    public String accessToken;

    /**
     * Дата и время истечения действия токена в миллисекундах, в формате unix timestamp.
     */
    @JsonProperty("expires_at")
    public long expiresAt;
}
