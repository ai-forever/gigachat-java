package chat.giga.client;

import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.http.client.HttpHeaders;
import chat.giga.http.client.HttpMethod;
import chat.giga.http.client.HttpRequest;
import chat.giga.http.client.HttpResponse;
import chat.giga.http.client.MediaType;
import chat.giga.http.client.sse.SseListener;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.completion.ChatFunctionCallEnum;
import chat.giga.model.completion.CompletionChunkResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.util.JsonUtils;
import chat.giga.util.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GigaChatClientAsyncImplTest {

    @Mock
    HttpClient httpClient;
    @Mock
    AuthClient authClient;
    @Mock
    ResponseHandler<CompletionChunkResponse> completionChunkResponseHandler;

    GigaChatClientAsync gigaChatClientAsync;

    ObjectMapper objectMapper = JsonUtils.objectMapper();

    @BeforeEach
    void setUp() {
        gigaChatClientAsync = GigaChatClientAsync.builder()
                .apiHttpClient(httpClient)
                .authClient(authClient)
                .build();
    }

    @Test
    void models() throws Exception {
        var body = TestData.modelResponse();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var response = gigaChatClientAsync.models().get();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/models");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void completions() throws Exception {
        var body = TestData.completionResponse();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var request = TestData.completionRequest()
                .toBuilder()
                .functionCall(ChatFunctionCallEnum.AUTO)
                .build();
        var response = gigaChatClientAsync.completions(request).get();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

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
    void completionsProfanityCheck() throws Exception {
        var body = TestData.completionResponse();
        when(httpClient.executeAsync(any())).thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                .body(objectMapper.writeValueAsBytes(body))
                .build()));

        var request = TestData.completionRequest()
                .toBuilder()
                .profanityCheck(true)
                .build();
        var response = gigaChatClientAsync.completions(request).get();

        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).executeAsync(captor.capture());

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
    void completionStream() {
        var body = TestData.completionChunkResponse();
        doAnswer(i -> {
            var listener = i.getArgument(1, SseListener.class);
            listener.onError(new HttpClientException(401, null));

            return null;
        }).doAnswer(i -> {
            var listener = i.getArgument(1, SseListener.class);
            listener.onData(objectMapper.writeValueAsString(body));
            listener.onComplete();

            return null;
        }).when(httpClient).execute(any(), any());

        var request = TestData.completionRequest();
        gigaChatClientAsync.completions(request, completionChunkResponseHandler);

        verify(authClient, timeout(100).times(2)).authenticate(any());

        var requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, timeout(100).times(2)).execute(requestCaptor.capture(), any());

        assertThat(requestCaptor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/chat/completions");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.TEXT_EVENT_STREAM));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
            assertThat(objectMapper.readValue(r.body(), CompletionRequest.class)).isEqualTo(request.toBuilder()
                    .stream(true)
                    .build());
        });

        var responseCaptor = ArgumentCaptor.forClass(CompletionChunkResponse.class);
        verify(completionChunkResponseHandler, timeout(100)).onNext(responseCaptor.capture());
        verify(completionChunkResponseHandler, timeout(100)).onComplete();
        verify(completionChunkResponseHandler, after(100).never()).onError(any());

        assertThat(responseCaptor.getValue()).isEqualTo(body);
    }

    @Test
    void completionStreamFailedWhenRetryExhausted() {
        doAnswer(i -> {
            var listener = i.getArgument(1, SseListener.class);
            listener.onError(new HttpClientException(401, null));

            return null;
        }).doAnswer(i -> {
            var listener = i.getArgument(1, SseListener.class);
            listener.onError(new HttpClientException(401, null));

            return null;
        }).when(httpClient).execute(any(), any());

        var request = TestData.completionRequest();
        gigaChatClientAsync.completions(request, completionChunkResponseHandler);

        verify(authClient, timeout(100).times(2)).authenticate(any());
        verify(httpClient, timeout(100).times(2)).execute(any(), any());

        verify(completionChunkResponseHandler, timeout(100)).onError(any(IllegalStateException.class));
    }

    @Test
    void completionStreamFailed() {
        doAnswer(i -> {
            var listener = i.getArgument(1, SseListener.class);
            listener.onError(new RuntimeException());

            return null;
        }).when(httpClient).execute(any(), any());

        gigaChatClientAsync.completions(CompletionRequest.builder().build(),
                completionChunkResponseHandler);

        verify(completionChunkResponseHandler, timeout(100)).onError(any(RuntimeException.class));
    }

    @Test
    void embeddings() throws Exception {
        var body = TestData.embeddingResponse();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var request = TestData.embeddingRequest();
        var response = gigaChatClientAsync.embeddings(request).get();

        verify(authClient, times(2)).authenticate(any());

        assertThat(response).isEqualTo(body);

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

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
    void uploadFile() throws Exception {
        var body = TestData.fileResponse();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var request = TestData.uploadFileRequest();
        var response = gigaChatClientAsync.uploadFile(request).get();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

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
    void downloadFileWithXClientIdNull() throws Exception {
        var body = new byte[100];
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(body)
                        .build()));

        var fileId = UUID.randomUUID().toString();
        var response = gigaChatClientAsync.downloadFile(fileId, null).get().readAllBytes();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId + "/content");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_OCTET_STREAM));
        });
    }

    @Test
    void downloadFileWithXClientIdNotNull() throws Exception {
        var body = new byte[100];
        when(httpClient.executeAsync(any())).thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                .body(body)
                .build()));

        var fileId = UUID.randomUUID().toString();
        var clientId = UUID.randomUUID().toString();
        var response = gigaChatClientAsync.downloadFile(fileId, clientId).get().readAllBytes();

        assertThat(response).isEqualTo(body);

        verify(authClient).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).executeAsync(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId + "/content");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_OCTET_STREAM));
            assertThat(r.headers()).containsEntry(GigaChatClientImpl.CLIENT_ID_HEADER, List.of(clientId));
        });
    }

    @Test
    void availableFileList() throws Exception {
        var body = TestData.availableFilesResponse();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var response = gigaChatClientAsync.availableFileList().get();
        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void fileInfo() throws Exception {
        var body = TestData.fileResponse();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var fileId = UUID.randomUUID().toString();
        var response = gigaChatClientAsync.fileInfo(fileId).get();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId);
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void deleteFile() throws Exception {
        var body = TestData.fileDeletedResponse();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var fileId = UUID.randomUUID().toString();
        var response = gigaChatClientAsync.deleteFile(fileId).get();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/files/" + fileId + "/delete");
            assertThat(r.method()).isEqualTo(HttpMethod.POST);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void tokensCount() throws Exception {
        var body = TestData.tokenCounts();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var request = TestData.tokenCountRequest();
        var tokenCounts = gigaChatClientAsync.tokensCount(request).get();

        assertThat(tokenCounts).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

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
    void balance() throws Exception {
        var body = TestData.balanceResponse();
        when(httpClient.executeAsync(any()))
                .thenReturn(CompletableFuture.failedFuture(new HttpClientException(401, null)))
                .thenReturn(CompletableFuture.completedFuture(HttpResponse.builder()
                        .body(objectMapper.writeValueAsBytes(body))
                        .build()));

        var response = gigaChatClientAsync.balance().get();

        assertThat(response).isEqualTo(body);

        verify(authClient, times(2)).authenticate(any());

        var captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient, times(2)).executeAsync(captor.capture());

        assertThat(captor.getValue()).satisfies(r -> {
            assertThat(r.url()).isEqualTo(GigaChatClientImpl.DEFAULT_API_URL + "/balance");
            assertThat(r.method()).isEqualTo(HttpMethod.GET);
            assertThat(r.headers()).containsEntry(HttpHeaders.USER_AGENT, List.of(BaseGigaChatClient.USER_AGENT_NAME));
            assertThat(r.headers()).containsEntry(HttpHeaders.ACCEPT, List.of(MediaType.APPLICATION_JSON));
            assertThat(r.headers()).containsKey(GigaChatClientImpl.REQUEST_ID_HEADER);
        });
    }

    @Test
    void clientWithNullParamsThrowsException() {
        assertThrows(NullPointerException.class, () -> GigaChatClientImpl.builder()
                .apiHttpClient(httpClient)
                .build());
    }
}
