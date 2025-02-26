package chat.giga.client;

import chat.giga.model.*;

import java.util.concurrent.CompletableFuture;

public interface GigaChatClientAsync {

    CompletableFuture<ModelsResponse> models();

    CompletableFuture<CompletionsResponse> completions(CompletionsRequest request);

    CompletableFuture<EmbeddingsResponse> embeddings(EmbeddingsRequest request);

    CompletableFuture<UploadFileResponse> uploadFile(UploadFileRequest request);

    CompletableFuture<DownloadFileResponse> downloadFile(DownloadFileRequest request);

    CompletableFuture<TokensCountResponse> tokensCount(TokensCountResponse request);
}
