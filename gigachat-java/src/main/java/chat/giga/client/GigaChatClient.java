package chat.giga.client;

import chat.giga.model.*;

public interface GigaChatClient {

    ModelsResponse models();

    CompletionsResponse completions(CompletionsRequest request);

    EmbeddingsResponse embeddings(EmbeddingsRequest request);

    UploadFileResponse uploadFile(UploadFileRequest request);

    DownloadFileResponse downloadFile(DownloadFileRequest request);

    TokensCountResponse tokensCount(TokensCountResponse request);
}
