package chat.giga.client;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpResponse;
import chat.giga.http.client.MediaType;
import chat.giga.model.Balance;
import chat.giga.model.BalanceResponse;
import chat.giga.model.TokenCount;
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
import chat.giga.model.embedding.Embedding;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.model.embedding.EmbeddingUsage;
import chat.giga.model.file.FileResponse;
import chat.giga.model.file.ListAvailableFileResponse;
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
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    void balance() throws JsonProcessingException {
        var body = BalanceResponse.builder()
                .addBalance(Balance.builder()
                        .usage("testModel")
                        .value(100)
                        .build())
                .build();

        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(new ByteArrayInputStream(objectMapper.writeValueAsBytes(body)))
                .build());

        var response = gigaChatClient.balance();

        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/balance");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Bearer testToken"));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
        });
    }

    @Test
    void embeddings() throws JsonProcessingException {
        var request = EmbeddingRequest.builder()
                .model("Embeddings")
                .input(List.of("Расскажи о современных технологиях"))
                .build();
        var body = EmbeddingResponse.builder()
                .model("Embeddings")
                .object("list")
                .data(List.of(Embedding.builder()
                        .usage(EmbeddingUsage.builder()
                                .promptTokens(11)
                                .build())
                        .object("embedding")
                        .embedding(List.of())
                        .index(0)
                        .build()))
                .build();
        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(new ByteArrayInputStream(objectMapper.writeValueAsBytes(body)))
                .build());
        var response = gigaChatClient.embeddings(request);

        assertThat(response).isEqualTo(body);
        assertThat(response.model()).isEqualTo(body.model());
        assertThat(response.data()).isEqualTo(body.data());
        assertThat(response.object()).isEqualTo(body.object());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/embeddings");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Bearer testToken"));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
        });
    }

    @Test
    void downloadFileWithXClientIdNull() {
        var fileId = UUID.randomUUID().toString();
        var body = new byte[10000];
        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(new ByteArrayInputStream(body))
                .build());
        var response = gigaChatClient.downloadFile(fileId, null);
        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId + "/content");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.IMAGE_JPG));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Bearer testToken"));
        });
    }

    @Test
    void downloadFileWithXClientIdNotNull() {
        var fileId = UUID.randomUUID().toString();
        var xClientId = UUID.randomUUID().toString();
        var body = new byte[10000];
        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(new ByteArrayInputStream(body))
                .build());
        var response = gigaChatClient.downloadFile(fileId, xClientId);
        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId + "/content");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.IMAGE_JPG));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Bearer testToken"));
            assertThat(r.headers()).containsEntry(HttpHeaders.X_CLIENT_ID, List.of(xClientId));
        });
    }

    @Test
    void getListAvailableFile() throws JsonProcessingException {
        var body = ListAvailableFileResponse.builder()
                .data(List.of(FileResponse.builder()
                        .accessPolicy("testPolicy")
                        .bytes(100)
                        .createdAt(1740942137)
                        .filename("test")
                        .id(UUID.randomUUID())
                        .purpose("general")
                        .object("file")
                        .build()))
                .build();
        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(new ByteArrayInputStream(objectMapper.writeValueAsBytes(body)))
                .build());
        var response = gigaChatClient.getListAvailableFile();
        assertThat(response).isEqualTo(body);
        assertThat(response.data()).isEqualTo(body.data());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.AUTHORIZATION, List.of("Bearer testToken"));
        });
    }
}
