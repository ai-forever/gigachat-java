package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpResponse;
import chat.giga.http.client.MediaType;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthClientTest {

    String clientId = "clientId";
    String secret = "secret";
    String token = "secret";
    Scope scope = Scope.GIGACHAT_API_PERS;

    @Mock
    HttpClient httpClient;

    ObjectMapper objectMapper = JsonUtils.objectMapper();

    @Test
    public void oauth() throws Exception {
        OAuthClient authClient = new OAuthClient(httpClient, clientId, secret, scope, null);
        AccessTokenResponse mockResponse = AccessTokenResponse.builder()
                .accessToken(token)
                .expiresAt(Instant.now()
                        .plusSeconds(1000L).toEpochMilli())
                .build();
        when(httpClient.execute(any())).thenReturn(getHttpResponse(mockResponse));

        AccessTokenResponse response = authClient.oauth();

        assertThat(response).isEqualTo(mockResponse);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());
        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.url()).asString().isEqualTo("https://ngw.devices.sberbank.ru:9443/api/v2/oauth");
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT,
                    List.of(TokenBasedAuthClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Basic Y2xpZW50SWQ6c2VjcmV0"));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE,
                    List.of(MediaType.APPLICATION_X_WWW_FORM_URLENCODED));
            assertThat(r.headers()).containsKey(OAuthClient.RQ_UID_HEADER);
            assertThat(new String(r.body(), StandardCharsets.UTF_8)).isEqualTo("scope=" + scope.name());
        });
    }

    @Test
    public void retrieveTokenWhenTokenIsNew() throws Exception {
        OAuthClient authClient = new OAuthClient(httpClient, clientId, secret, scope, null);
        AccessTokenResponse mockResponse = AccessTokenResponse.builder()
                .accessToken(token)
                .expiresAt(Instant.now()
                        .plusSeconds(1000L).toEpochMilli())
                .build();
        when(httpClient.execute(any())).thenReturn(getHttpResponse(mockResponse));

        String response = authClient.retrieveTokenIfExpired();

        assertThat(response).isEqualTo(mockResponse.accessToken());
        verify(httpClient, times(1)).execute(any());
    }

    @Test
    public void retrieveTokenWhenTokenExistsAndIsOk() throws Exception {
        OAuthClient authClient = new OAuthClient(httpClient, clientId, secret, scope, null);
        AccessTokenResponse mockResponse = AccessTokenResponse.builder()
                .accessToken(token)
                .expiresAt(Instant.now()
                        .plusSeconds(1000L).toEpochMilli())
                .build();
        when(httpClient.execute(any())).thenReturn(getHttpResponse(mockResponse));

        String response = authClient.retrieveTokenIfExpired();

        String response2 = authClient.retrieveTokenIfExpired();

        assertThat(response).isEqualTo(mockResponse.accessToken());
        assertThat(response2).isEqualTo(mockResponse.accessToken());
        verify(httpClient, times(1)).execute(any());
    }

    @Test
    public void retrieveTokenTokenWhenTokenExistsAndIsExpired() throws Exception {
        OAuthClient authClient = new OAuthClient(httpClient, clientId, secret, scope, null);
        AccessTokenResponse mockResponse = AccessTokenResponse.builder()
                .accessToken(token)
                .expiresAt(Instant.now()
                        .minusSeconds(1000L).toEpochMilli())
                .build();
        when(httpClient.execute(any())).thenReturn(getHttpResponse(mockResponse))
                .thenReturn(getHttpResponse(mockResponse));

        String response = authClient.retrieveTokenIfExpired();
        String response2 = authClient.retrieveTokenIfExpired();

        assertThat(response).isEqualTo(mockResponse.accessToken());
        assertThat(response2).isEqualTo(mockResponse.accessToken());
        verify(httpClient, times(2)).execute(any());
    }

    @Test
    public void checkCustomUrl() throws Exception {
        OAuthClient authClient = new OAuthClient(httpClient, clientId, secret, scope,
                "https://test.com");
        AccessTokenResponse mockResponse = AccessTokenResponse.builder()
                .accessToken("test")
                .expiresAt(Instant.now()
                        .plusSeconds(1000L).toEpochMilli())
                .build();
        when(httpClient.execute(any())).thenReturn(getHttpResponse(mockResponse));

        authClient.oauth();

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());
        assertThat(captor.getValue()).satisfies(r ->
                assertThat(r.url()).asString().isEqualTo("https://test.com/oauth"));
    }

    private HttpResponse getHttpResponse(AccessTokenResponse mockResponse) throws JsonProcessingException {
        return HttpResponse.builder()
                .body(objectMapper.writeValueAsBytes(mockResponse))
                .build();
    }
}

