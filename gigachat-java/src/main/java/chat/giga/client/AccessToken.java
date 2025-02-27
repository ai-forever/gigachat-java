package chat.giga.client;

import lombok.Data;

import java.time.Instant;

@Data
public class AccessToken {

    private final String token;
    private final Instant expiresAt;


    public AccessToken(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
