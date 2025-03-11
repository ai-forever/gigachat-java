package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CertificateAuthClientTest {

    @Mock
    HttpClient httpClient;

    @Test
    public void assertMethods() {
        CertificateAuthClient authClient = new CertificateAuthClient(httpClient);
        assertNotNull(authClient.getHttpClient());
        assertEquals(authClient.getHttpClient(), httpClient);
        assertTrue(authClient.supportsHttpClient());
    }

    @Test
    public void assertErrorHttpClientNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new CertificateAuthClient(null));

        assertEquals("httpClient must not be null", exception.getMessage());
    }

    @Test
    public void authenticate() {
        HttpRequestBuilder requestBuilder = HttpRequest.builder()
                .url("test.ru/models");

        AuthClient authClient = AuthClientBuilder.builder().withCertificatesAuth(httpClient).build();

        authClient.authenticate(requestBuilder);

        assertEquals(0, requestBuilder.build().headers().size());

    }
}
