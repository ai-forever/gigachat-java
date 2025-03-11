package chat.giga.client.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthClientBuilderTest {

    @Test
    public void withOAuthNull() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                AuthClient.builder().build());

        assertEquals("Authentication method not specified", exception.getMessage());
    }
}
