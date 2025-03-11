package chat.giga.client.auth;

import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProvidedTokenAuthClientTest {

    String token = "token";

    @Test
    public void assertMethods() {
        ProvidedTokenAuthClient authClient = new ProvidedTokenAuthClient(token);
        assertNull(authClient.getHttpClient());
        assertFalse(authClient.supportsHttpClient());
    }

    @Test
    public void assertErrorClientIdNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new ProvidedTokenAuthClient(null));

        assertEquals("accessToken must not be null", exception.getMessage());
    }

    @Test
    public void authenticate() {
        AuthClient authClient = AuthClientBuilder.builder()
                .withProvidedTokenAuth(token)
                .build();

        HttpRequestBuilder builder = HttpRequest.builder()
                .url("test.ru/models");

        authClient.authenticate(builder);

        assertThat(builder.build().headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Bearer " + token));
    }
}
