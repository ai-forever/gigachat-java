package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpResponse;
import chat.giga.http.client.MediaType;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.completion.ChatFunction;
import chat.giga.model.completion.ChatFunctionCall;
import chat.giga.model.completion.ChatFunctionsFewShotExamples;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessage.Role;
import chat.giga.model.completion.Choice;
import chat.giga.model.completion.Choice.FinishReason;
import chat.giga.model.completion.ChoiceMessage;
import chat.giga.model.completion.ChoiceMessageFunctionCall;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.completion.Usage;
import chat.giga.model.token.TokenCount;
import chat.giga.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GigaChatClientImplTest {

    @Mock
    HttpClient httpClient;

    GigaChatClient gigaChatClient;

    ObjectMapper objectMapper = JsonUtils.objectMapper();

    @BeforeEach
    void setUp() {
        gigaChatClient = GigaChatClientImpl.builder()
                .apiHttpClient(httpClient)
                .accessToken("testToken")
                .build();
    }

    @Test
    void completions() throws JsonProcessingException {
        var request = CompletionRequest.builder()
                .model("testModel")
                .message(ChatMessage.builder()
                        .role(Role.SYSTEM)
                        .content("test")
                        .functionsStateId("testState")
                        .attachment("testAttachment")
                        .build())
                .functionCall(ChatFunctionCall.builder()
                        .name("testFunc")
                        .partialArguments("testArgs")
                        .build())
                .function(ChatFunction.builder()
                        .name("testFunc")
                        .description("testDescription")
                        .parameters("testParams")
                        .fewShotExample(ChatFunctionsFewShotExamples.builder()
                                .request("test")
                                .param("testParam", "testVal")
                                .build())
                        .returnParameters("testReturnParams")
                        .build())
                .temperature(0.5f)
                .topP(0.7f)
                .maxTokens(1)
                .repetitionPenalty(0.1f)
                .updateInterval(2)
                .build();

        var body = CompletionResponse.builder()
                .choice(Choice.builder()
                        .message(ChoiceMessage.builder()
                                .role(ChoiceMessage.Role.ASSISTANT)
                                .content("test")
                                .created(1234)
                                .name("testFunc")
                                .functionsStateId("testState")
                                .functionCall(ChoiceMessageFunctionCall.builder()
                                        .name("testFunc")
                                        .argument("testArg", "testVal")
                                        .build())
                                .build())
                        .index(0)
                        .finishReason(FinishReason.STOP)
                        .build())
                .created(3214)
                .model("testModel")
                .usage(Usage.builder()
                        .promptTokens(1)
                        .completionTokens(2)
                        .totalTokens(3)
                        .build())
                .object("test")
                .build();

        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(new ByteArrayInputStream(objectMapper.writeValueAsBytes(body)))
                .build());

        var response = gigaChatClient.completions(request);

        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/chat/completions");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Bearer testToken"));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            assertThat(objectMapper.readValue(r.body(), CompletionRequest.class)).isEqualTo(request);
        });
    }

    @Test
    void tokensCount() throws JsonProcessingException {
        var request = TokenCountRequest.builder()
                .model("testModel")
                .addInput("test")
                .build();
        var body = List.of(TokenCount.builder()
                .tokens(1)
                .characters(2)
                .build());

        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(new ByteArrayInputStream(objectMapper.writeValueAsBytes(body)))
                .build());

        var tokenCounts = gigaChatClient.tokensCount(request);

        assertThat(tokenCounts).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/tokens/count");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Bearer testToken"));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            assertThat(objectMapper.readValue(r.body(), TokenCountRequest.class)).isEqualTo(request);
        });
    }
}
