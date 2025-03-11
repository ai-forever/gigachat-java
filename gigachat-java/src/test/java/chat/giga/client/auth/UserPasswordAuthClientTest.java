package chat.giga.client.auth;

import chat.giga.client.auth.AuthClientBuilder.UserPasswordAuthBuilder;
import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpRequest.HttpRequestBuilder;
import chat.giga.http.client.HttpResponse;
import chat.giga.http.client.MediaType;
import chat.giga.model.AccessTokenByUserPasswordResponse;
import chat.giga.model.Scope;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPasswordAuthClientTest {

    String user = "clientId";
    String pass = "secret";
    String token = "secret";
    String url = "https://test.ru";
    Scope scope = Scope.GIGACHAT_API_PERS;

    @Mock
    HttpClient httpClient;

    ObjectMapper objectMapper = JsonUtils.objectMapper();

    @Test
    public void assertMethods() {
        UserPasswordAuthClient authClient = new UserPasswordAuthClient(httpClient, user, pass, scope, url);
        assertNull(authClient.getHttpClient());
        assertFalse(authClient.supportsHttpClient());
    }

    @Test
    public void assertErrorUserIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new UserPasswordAuthClient(httpClient, null, pass, scope, url));

        assertEquals("user must not be null", exception.getMessage());
    }

    @Test
    public void assertErrorPasswordIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new UserPasswordAuthClient(httpClient, user, null, scope, url));

        assertEquals("password must not be null", exception.getMessage());
    }

    @Test
    public void assertErrorAuthApiUrlIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new UserPasswordAuthClient(httpClient, user, pass, scope, null));

        assertEquals("authApiUrl must not be null", exception.getMessage());
    }

    @Test
    public void assertErrorScopeIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new UserPasswordAuthClient(httpClient, user, pass, null, url));

        assertEquals("scope must not be null", exception.getMessage());
    }

    @Test
    public void authenticate() throws Exception {
        AuthClient authClient = AuthClientBuilder.builder()
                .withUserPassword(UserPasswordAuthBuilder.builder()
                        .user(user)
                        .password(pass)
                        .scope(scope)
                        .httpClient(httpClient)
                        .authApiUrl(url)
                        .build())
                .build();

        AccessTokenByUserPasswordResponse mockResponse = AccessTokenByUserPasswordResponse.builder()
                .accessToken(token)
                .expiresAt(Instant.now()
                        .plusSeconds(1000L).toEpochMilli())
                .build();
        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(objectMapper.writeValueAsBytes(mockResponse))
                .build());

        HttpRequestBuilder requestBuilder = HttpRequest.builder()
                .url("test.ru/models");

        authClient.authenticate(requestBuilder);
        assertThat(requestBuilder.build().headers()).containsEntry(HttpHeaders.AUTHORIZATION,
                List.of("Bearer " + token));

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());
        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.url()).asString().isEqualTo("https://test.ru/token");
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT,
                    List.of(TokenBasedAuthClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Basic Y2xpZW50SWQ6c2VjcmV0"));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE,
                    List.of(MediaType.APPLICATION_X_WWW_FORM_URLENCODED));
            assertThat(new String(r.body(), StandardCharsets.UTF_8)).isEqualTo("scope=" + scope.name());
        });
    }

}
