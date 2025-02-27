package chat.giga.client;

import chat.giga.model.*;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;

public interface GigaChatClient {


    /**
     * @return массив объектов с данными доступных моделей.
     */
    ModelsResponse models();


    /**
     * @return ответ модели, сгенерированный на основе переданных сообщений.
     */
    CompletionsResponse completions(CompletionsRequest request);

    /**
     * @return векторные представления соответствующих текстовых запросов. Векторное представление выглядит как
     * массив чисел `embedding`. Каждое значение в массиве представляет одну из характеристик или признаков текста,
     * учтенных при вычислении эмбеддинга. Значения образуют числовое представление текста и позволяют анализировать и
     * использовать текст в различных задачах.
     *
     */
    EmbeddingResponse embeddings(EmbeddingRequest request);

    /**
     * Загружает в хранилище текстовые документы или изображения.
     * @return объект с данными загруженного файла.
     * Загруженные файлы доступны только вам
     *
     * @param request
     *
     */
    UploadFileResponse uploadFile(UploadFileRequest request);

    /**
     * @param request
     * @return массив объектов с данными доступных файлов.
     */
    DownloadFileResponse downloadFile(DownloadFileRequest request);

    /**
     * @param request
     * @return объект с информацией о количестве токенов, подсчитанных заданной моделью в строках.
     * Строки передаются в массиве input.
     */
    TokensCountResponse tokensCount(TokensCountResponse request);
}
