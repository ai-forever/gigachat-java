package chat.giga.client;

import chat.giga.model.*;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;

import java.util.concurrent.CompletableFuture;

public interface GigaChatClientAsync {

    CompletableFuture<ModelsResponse> models();

    CompletableFuture<CompletionsResponse> completions(CompletionsRequest request);

    CompletableFuture<EmbeddingResponse> embeddings(EmbeddingRequest request);

    CompletableFuture<UploadFileResponse> uploadFile(UploadFileRequest request);

    CompletableFuture<DownloadFileResponse> downloadFile(DownloadFileRequest request);

    CompletableFuture<TokensCountResponse> tokensCount(TokensCountResponse request);
}
