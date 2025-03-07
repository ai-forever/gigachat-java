package chat.giga.client.auth;

import java.time.Instant;

public record AccessToken(String token, Instant expiresAt) {

}
