package chat.giga.client;

import chat.giga.model.AccessTokenResponse;

public interface GigaChatAuthClient {

    String retrieveTokenIfExpired();

    AccessTokenResponse oauth();
}
