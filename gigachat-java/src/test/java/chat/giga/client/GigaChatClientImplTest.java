package chat.giga.client;

import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpResponse;
import chat.giga.http.client.MediaType;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.batch.BatchMethod;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.filter.FilterCheckRequest;
import chat.giga.model.v2.completion.ChatMessageV2;
import chat.giga.model.v2.completion.CompletionRequestV2;
import chat.giga.model.v2.completion.CompletionResponseV2;
import chat.giga.model.v2.completion.FunctionResultContentV2;
import chat.giga.model.v2.completion.FunctionSpecificationV2;
import chat.giga.model.v2.completion.FunctionsToolPayloadV2;
import chat.giga.model.v2.completion.MessageContentPartV2;
import chat.giga.model.v2.completion.ToolConfigV2;
import chat.giga.model.v2.completion.ToolV2;
import chat.giga.util.JsonUtils;
import chat.giga.util.TestData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static chat.giga.client.BaseGigaChatClient.SESSION_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GigaChatClientImplTest {

    @Mock
    HttpClient httpClient;
    @Mock
    AuthClient authClient;

    GigaChatClient gigaChatClient;

    ObjectMapper objectMapper = JsonUtils.objectMapper();

    @BeforeEach
    void setUp() {
        gigaChatClient = GigaChatClientImpl.builder()
                .apiHttpClient(httpClient)
                .authClient(authClient)
                .build();
    }

    @Test
    void deriveApiV2Url() {
        assertThat(BaseGigaChatClient.deriveApiV2Url("https://example.com/api/v1")).isEqualTo(
                "https://example.com/api/v2");
        assertThat(BaseGigaChatClient.deriveApiV2Url(null)).isEqualTo(BaseGigaChatClient.DEFAULT_API_V2_URL);
    }

    @Test
    void completionsSessionId() throws JsonProcessingException {
        var body = TestData.completionResponse();

        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var request = TestData.completionRequest();
        var sessionId = "test-id";
        var response = gigaChatClient.completions(request, sessionId);

        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/chat/completions");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(SESSION_ID_HEADER, List.of(sessionId));
        });
    }

    @Test
    void completionsV2SessionId() throws JsonProcessingException {
        var body = CompletionResponseV2.builder()
                .model("GigaChat-3-Ultra:1.0")
                .createdAt(1769508838L)
                .finishReason("stop")
                .message(ChatMessageV2.textMessage("assistant", "ok"))
                .build();

        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var request = CompletionRequestV2.builder()
                .model("GigaChat")
                .message(ChatMessageV2.textMessage("user", "hi"))
                .tool(ToolV2.ofFunctions(FunctionsToolPayloadV2.builder().build()))
                .toolConfig(ToolConfigV2.autoMode())
                .build();
        var sessionId = "v2-session";
        var response = gigaChatClient.completionsV2(request, sessionId);

        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(BaseGigaChatClient.DEFAULT_API_V2_URL + "/chat/completions");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(SESSION_ID_HEADER, List.of(sessionId));
        });
    }

    @Test
    void completionsV2() throws JsonProcessingException {
        var body = CompletionResponseV2.builder()
                .model("GigaChat:1.0")
                .createdAt(1L)
                .finishReason("stop")
                .build();

        when(httpClient.execute(any()))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var schema = objectMapper.readTree("{\"type\":\"object\",\"properties\":{\"location\":{\"type\":\"string\"}}}");
        var request = CompletionRequestV2.builder()
                .model("GigaChat")
                .message(ChatMessageV2.builder()
                        .role("tool")
                        .contentPart(MessageContentPartV2.builder()
                                .functionResult(FunctionResultContentV2.builder()
                                        .name("weather")
                                        .result(TextNode.valueOf("{\"t\":1}"))
                                        .build())
                                .build())
                        .build())
                .tool(ToolV2.ofFunctions(FunctionsToolPayloadV2.builder()
                        .specification(FunctionSpecificationV2.builder()
                                .name("weather")
                                .description("forecast")
                                .parameters(schema)
                                .build())
                        .build()))
                .toolConfig(ToolConfigV2.forcedFunction("weather"))
                .build();

        var response = gigaChatClient.completionsV2(request);

        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(BaseGigaChatClient.DEFAULT_API_V2_URL + "/chat/completions");
            var json = objectMapper.readTree(r.body());
            assertThat(json.toString())
                    .as("v2 request JSON must use snake_case keys from @JsonProperty")
                    .contains("\"tool_config\"")
                    .contains("\"function_name\"")
                    .contains("\"forced\"")
                    .contains("\"specifications\"");
            assertThat(json.get("model").asText()).isEqualTo("GigaChat");
            assertThat(json.get("messages").get(0).get("role").asText()).isEqualTo("tool");
        });
    }

    @Test
    void completions() throws JsonProcessingException {
        var body = TestData.completionResponse();

        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var request = TestData.completionRequest();
        var response = gigaChatClient.completions(request);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/chat/completions");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            System.out.println(new String(r.body()));
            assertThat(objectMapper.readValue(r.body(), CompletionRequest.class)).isEqualTo(request);
        });
    }

    @Test
    void completionsProfanityCheck() throws JsonProcessingException {
        var body = TestData.completionResponse();
        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(objectMapper.writeValueAsBytes(body))
                .build());

        var request = TestData.completionRequest()
                .toBuilder()
                .profanityCheck(true)
                .build();
        var response = gigaChatClient.completions(request);

        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/chat/completions");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            assertThat(objectMapper.readValue(r.body(), CompletionRequest.class)).isEqualTo(request);
        });
    }

    @Test
    void tokensCount() throws JsonProcessingException {
        var body = TestData.tokenCounts();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var request = TestData.tokenCountRequest();
        var tokenCounts = gigaChatClient.tokensCount(request);

        assertThat(tokenCounts).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/tokens/count");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            assertThat(objectMapper.readValue(r.body(), TokenCountRequest.class)).isEqualTo(request);
        });
    }

    @Test
    void balance() throws JsonProcessingException {
        var body = TestData.balanceResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var response = gigaChatClient.balance();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/balance");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
        });
    }

    @Test
    void embeddings() throws JsonProcessingException {
        var body = TestData.embeddingResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var request = TestData.embeddingRequest();
        var response = gigaChatClient.embeddings(request);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/embeddings");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            assertThat(objectMapper.readValue(r.body(), EmbeddingRequest.class)).isEqualTo(request);
        });
    }

    @Test
    void downloadFileWithXClientIdNull() {
        var body = new byte[100];
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(body)
                        .build());

        var fileId = UUID.randomUUID().toString();
        var response = gigaChatClient.downloadFile(fileId, null);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId + "/content");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_OCTET_STREAM));
        });
    }

    @Test
    void downloadFileWithXClientIdNotNull() {
        var body = new byte[100];
        when(httpClient.execute(any())).thenReturn(HttpResponse.builder()
                .body(body)
                .build());

        var fileId = UUID.randomUUID().toString();
        var clientId = UUID.randomUUID().toString();
        var response = gigaChatClient.downloadFile(fileId, clientId);

        assertThat(response).isEqualTo(body);

        verify(authClient).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId + "/content");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_OCTET_STREAM));
            assertThat(r.headers()).containsEntry(GigaChatClientImpl.CLIENT_ID_HEADER, List.of(clientId));
        });
    }

    @Test
    void availableFileList() throws JsonProcessingException {
        var body = TestData.availableFilesResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var response = gigaChatClient.availableFileList();
        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void uploadFile() throws IOException {
        var body = TestData.fileResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var request = TestData.uploadFileRequest();
        var response = gigaChatClient.uploadFile(request);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers().get(HttpHeaders.CONTENT_TYPE)).isNotEmpty();
            assertThat(r.headers().get(HttpHeaders.CONTENT_TYPE).get(0)).contains(
                    MediaType.MULTIPART_FORM_DATA + "; boundary");
            assertThat(r.body()).isNotEmpty();
        });
    }

    @Test
    void fileInfo() throws JsonProcessingException {
        var body = TestData.fileResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var fileId = UUID.randomUUID().toString();
        var response = gigaChatClient.fileInfo(fileId);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId);
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void deleteFile() throws JsonProcessingException {
        var body = TestData.fileDeletedResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var fileId = UUID.randomUUID().toString();
        var response = gigaChatClient.deleteFile(fileId);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId + "/delete");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void models() throws JsonProcessingException {
        var body = TestData.modelResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var response = gigaChatClient.models();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/models");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void createBatch() throws JsonProcessingException {
        var body = TestData.batchCreateResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var jsonlBody = "{\"custom\":\"jsonl\"}".getBytes(StandardCharsets.UTF_8);
        var method = BatchMethod.CHAT_COMPLETIONS;
        var response = gigaChatClient.createBatch(jsonlBody, method);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/batches?method=" + method.value());
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE,
                    List.of(MediaType.APPLICATION_OCTET_STREAM));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            assertThat(r.body()).isEqualTo(jsonlBody);
        });
    }

    @Test
    void batchStatus() throws JsonProcessingException {
        var body = TestData.batchStatusResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var batchId = "batch-id-123";
        var response = gigaChatClient.batchStatus(batchId);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/batches?batch_id=" + batchId);
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
        });
    }

    @Test
    void clientWithNullParamsThrowsException() {
        assertThrows(NullPointerException.class, () -> GigaChatClient.builder()
                .apiHttpClient(httpClient)
                .build());
    }

    @Test
    void filterCheck() throws JsonProcessingException {
        var body = TestData.filterCheckResponse();
        when(httpClient.execute(any()))
                .thenThrow(new HttpClientException(401, null))
                .thenReturn(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build());

        var request = TestData.filterCheckRequest();
        var response = gigaChatClient.filterCheck(request);

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).execute(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/filter/check");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            assertThat(objectMapper.readValue(r.body(), FilterCheckRequest.class)).isEqualTo(request);
        });
    }
}
