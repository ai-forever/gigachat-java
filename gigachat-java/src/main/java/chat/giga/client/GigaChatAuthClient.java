package chat.giga.client;

import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;

public interface GigaChatAuthClient {
    AccessTokenResponse oauth(String clientId, String secret, Scope scope);
}
