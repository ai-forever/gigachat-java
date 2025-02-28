package chat.giga.client;

import chat.giga.model.DownloadFileRequest;
import chat.giga.model.DownloadFileResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.UploadFileRequest;
import chat.giga.model.UploadFileResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.model.token.TokenCount;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GigaChatClientAsync {

    CompletableFuture<ModelResponse> models();

    CompletableFuture<CompletionResponse> completions(CompletionRequest request);

    CompletableFuture<EmbeddingResponse> embeddings(EmbeddingRequest request);

    CompletableFuture<UploadFileResponse> uploadFile(UploadFileRequest request);

    CompletableFuture<DownloadFileResponse> downloadFile(DownloadFileRequest request);

    CompletableFuture<List<TokenCount>> tokensCount(TokenCountRequest request);
}
