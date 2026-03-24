package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.GigaChatClientAsync;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpHeaders;
import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;
import chat.giga.model.v2.completion.ChatMessageV2;
import chat.giga.model.v2.completion.CompletionRequestV2;
import chat.giga.model.v2.completion.CompletionResponseV2;
import chat.giga.model.v2.completion.FunctionCallContentV2;
import chat.giga.model.v2.completion.FunctionResultContentV2;
import chat.giga.model.v2.completion.FunctionSpecificationV2;
import chat.giga.model.v2.completion.FunctionsToolPayloadV2;
import chat.giga.model.v2.completion.MessageContentPartV2;
import chat.giga.model.v2.completion.ToolConfigV2;
import chat.giga.model.v2.completion.ToolV2;
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
        var host = "http://localhost:" + mockServerClient.getPort();
        gigaChatClient = GigaChatClient.builder()
                .apiUrl(host)
                .apiV2Url(host)
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
                .apiUrl(host)
                .apiV2Url(host)
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

    @Test
    void completionsV2WithFunction() throws Exception {
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

        var parameters = objectMapper.readTree(
                "{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\"}},\"required\":[\"location\"]}");
        var functionsTool = ToolV2.ofFunctions(FunctionsToolPayloadV2.builder()
                .specification(FunctionSpecificationV2.builder()
                        .name("weather_forecast")
                        .description("Прогноз погоды")
                        .parameters(parameters)
                        .build())
                .build());

        var firstRequest = CompletionRequestV2.builder()
                .model("GigaChat")
                .message(ChatMessageV2.textMessage("user", "Погода в Москве на завтра"))
                .tool(functionsTool)
                .toolConfig(ToolConfigV2.autoMode())
                .build();

        var assistantWithCall = ChatMessageV2.builder()
                .role("assistant")
                .toolsStateId("tools-state-integration-1")
                .contentPart(MessageContentPartV2.builder()
                        .functionCall(FunctionCallContentV2.builder()
                                .name("weather_forecast")
                                .arguments(objectMapper.readTree("{\"location\":\"Moscow\"}"))
                                .build())
                        .build())
                .build();

        var firstResponse = CompletionResponseV2.builder()
                .model("GigaChat:1.0")
                .createdAt(1700000001L)
                .finishReason("function_call")
                .message(assistantWithCall)
                .build();

        mockServerClient.when(request("/chat/completions")
                        .withMethod("POST")
                        .withHeader(HttpHeaders.USER_AGENT, "GigaChat-java-lib")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                        .withHeader("X-Request-ID")
                        .withHeader(HttpHeaders.AUTHORIZATION, "Bearer test-access-token")
                        .withBody(objectMapper.writeValueAsString(firstRequest)))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(firstResponse)));

        var afterFirst = gigaChatClient.completionsV2(firstRequest);
        assertThat(afterFirst).isEqualTo(firstResponse);
        assertThat(afterFirst.messages().get(0).content().get(0).functionCall().name()).isEqualTo("weather_forecast");

        var secondRequest = CompletionRequestV2.builder()
                .model("GigaChat")
                .message(ChatMessageV2.textMessage("user", "Погода в Москве на завтра"))
                .message(assistantWithCall)
                .message(ChatMessageV2.builder()
                        .role("tool")
                        .toolsStateId("tools-state-integration-1")
                        .contentPart(MessageContentPartV2.builder()
                                .functionResult(FunctionResultContentV2.builder()
                                        .name("weather_forecast")
                                        .result(objectMapper.readTree("{\"temp_c\":5,\"condition\":\"cloudy\"}"))
                                        .build())
                                .build())
                        .build())
                .tool(functionsTool)
                .toolConfig(ToolConfigV2.autoMode())
                .build();

        var secondResponse = CompletionResponseV2.builder()
                .model("GigaChat:1.0")
                .createdAt(1700000002L)
                .finishReason("stop")
                .message(ChatMessageV2.textMessage("assistant", "Около 5 °C, облачно."))
                .build();

        mockServerClient.when(request("/chat/completions")
                        .withMethod("POST")
                        .withHeader(HttpHeaders.USER_AGENT, "GigaChat-java-lib")
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                        .withHeader("X-Request-ID")
                        .withHeader(HttpHeaders.AUTHORIZATION, "Bearer test-access-token")
                        .withBody(objectMapper.writeValueAsString(secondRequest)))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(objectMapper.writeValueAsString(secondResponse)));

        var afterSecond = gigaChatClient.completionsV2(secondRequest);
        assertThat(afterSecond).isEqualTo(secondResponse);

        assertThat(gigaChatClientAsync.completionsV2(firstRequest).get()).isEqualTo(firstResponse);
        assertThat(gigaChatClientAsync.completionsV2(secondRequest).get()).isEqualTo(secondResponse);
    }
}
