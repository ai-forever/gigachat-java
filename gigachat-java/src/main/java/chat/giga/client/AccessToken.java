package chat.giga.client;

import java.time.Instant;

public record AccessToken(String token, Instant expiresAt) {

}
