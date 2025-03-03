package chat.giga.client;

import chat.giga.model.BalanceResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.TokenCount;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.file.UploadFileRequest;
import chat.giga.model.file.FileResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GigaChatClientAsync {

    CompletableFuture<ModelResponse> models();

    CompletableFuture<CompletionResponse> completions(CompletionRequest request);

    CompletableFuture<EmbeddingResponse> embeddings(EmbeddingRequest request);

    CompletableFuture<FileResponse> uploadFile(UploadFileRequest request);

    CompletableFuture<ByteArrayInputStream> downloadFile();

    CompletableFuture<List<TokenCount>> tokensCount(TokenCountRequest request);

    CompletableFuture<BalanceResponse> balance();
}
