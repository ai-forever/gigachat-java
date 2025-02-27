package chat.giga.client;

import chat.giga.model.*;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;

public interface GigaChatClient {

    ModelsResponse models();

    CompletionsResponse completions(CompletionsRequest request);

    EmbeddingResponse embeddings(EmbeddingRequest request);

    UploadFileResponse uploadFile(UploadFileRequest request);

    DownloadFileResponse downloadFile(DownloadFileRequest request);

    TokensCountResponse tokensCount(TokensCountResponse request);
}
