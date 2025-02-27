package chat.giga.client;

import chat.giga.model.*;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;

import java.util.concurrent.CompletableFuture;

public interface GigaChatClientAsync {

    CompletableFuture<ModelResponse> models();

    CompletableFuture<CompletionResponse> completions(CompletionRequest request);

    CompletableFuture<EmbeddingResponse> embeddings(EmbeddingRequest request);

    CompletableFuture<UploadFileResponse> uploadFile(UploadFileRequest request);

    CompletableFuture<DownloadFileResponse> downloadFile(DownloadFileRequest request);

    CompletableFuture<TokenCountResponse> tokensCount(TokenCountResponse request);
}
