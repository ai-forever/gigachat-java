package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.GigaChatClientAsync;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpHeaders;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;
import chat.giga.util.JsonUtils;
import chat.giga.util.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.MediaType;

import java.time.Instant;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(MockServerExtension.class)
class GigaChatIntegrationTest {

    private final MockServerClient mockServerClient;

    GigaChatClient gigaChatClient;
    GigaChatClientAsync gigaChatClientAsync;

    private final ObjectMapper objectMapper = JsonUtils.objectMapper();

    public GigaChatIntegrationTest(MockServerClient mockServerClient) {
        this.mockServerClient = mockServerClient;
    }

    @BeforeEach
    void setUp() {
        gigaChatClient = GigaChatClient.builder()
                .apiUrl("http://localhost:" + mockServerClient.getPort())
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .authApiUrl("http://localhost:" + mockServerClient.getPort())
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId("test-client-id")
                                .clientSecret("test-secret")
                                .build())
                        .build())
                .build();

        gigaChatClientAsync = GigaChatClientAsync.builder()
                .apiUrl("http://localhost:" + mockServerClient.getPort())
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .authApiUrl("http://localhost:" + mockServerClient.getPort())
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId("test-client-id")
                                .clientSecret("test-secret")
                                .build())
                        .build())
                .build();
    }

    @Test
    void completions() throws Exception {
        mockServerClient.when(request("/oauth")
                        .withMethod("POST")
                        .withHeader(HttpHeaders.USER_AGENT, "GigaChat-java-lib")
                        .withContentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                        .withHeader(HttpHeaders.AUTHORIZATION,
                                "Basic " + Base64.getEncoder().encodeToString("test-client-id:test-secret".getBytes()))
                        .withHeader("RqUID")
                        .withBody("scope=" + Scope.GIGACHAT_API_PERS))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(AccessTokenResponse.builder()
                                .accessToken("test-access-token")
                                .expiresAt(Instant.now().plusSeconds(60).toEpochMilli())
                                .build())));

        var request = TestData.completionRequest();
        var body = TestData.completionResponse();
        mockServerClient.when(request("/chat/completions")
                        .withMethod("POST")
                        .withHeader(HttpHeaders.USER_AGENT, "GigaChat-java-lib")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                        .withHeader("X-Request-ID")
                        .withHeader(HttpHeaders.AUTHORIZATION, "Bearer test-access-token")
                        .withBody(objectMapper.writeValueAsString(request)))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(body)));

        var response = gigaChatClient.completions(request);
        assertThat(response).isEqualTo(body);

        response = gigaChatClientAsync.completions(request).get();
        assertThat(response).isEqualTo(body);
    }
}
