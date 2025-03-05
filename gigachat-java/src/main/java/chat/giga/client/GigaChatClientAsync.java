package chat.giga.client;

import chat.giga.client.GigaChatClientAsyncImpl.GigaChatClientAsyncImplBuilder;
import chat.giga.model.BalanceResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.TokenCount;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.completion.CompletionChunkResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.model.file.AvailableFilesResponse;
import chat.giga.model.file.FileDeletedResponse;
import chat.giga.model.file.FileResponse;
import chat.giga.model.file.UploadFileRequest;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GigaChatClientAsync {

    /**
     * Получить список моделей
     *
     * @return список данных доступных моделей.
     */
    CompletableFuture<ModelResponse> models();

    /**
     * Получить ответ модели на сообщения
     *
     * @param request описание запроса на получение ответа от модели
     * @return ответ модели, сгенерированный на основе переданных сообщений.
     */
    CompletableFuture<CompletionResponse> completions(CompletionRequest request);

    /**
     * Получить ответ модели на сообщения (stream = true)
     *
     * @param request описание запроса на получение ответа от модели
     * @param handler обработчик сообщений, сгенерированный на основе переданных сообщений.
     */
    CompletableFuture<Void> completions(CompletionRequest request, ResponseHandler<CompletionChunkResponse> handler);

    /**
     * Создать эмбендинг
     *
     * @param request описание запроса на получения эмбендинга
     * @return векторные представления соответствующих текстовых запросов. Векторное представление выглядит как массив
     * чисел `embedding`. Каждое значение в массиве представляет одну из характеристик или признаков текста, учтенных
     * при вычислении эмбеддинга. Значения образуют числовое представление текста и позволяют анализировать и
     * использовать текст в различных задачах.
     */
    CompletableFuture<EmbeddingResponse> embeddings(EmbeddingRequest request);

    /**
     * Загрузить файл
     *
     * @param request описание запроса запрос на загрузку файла
     * @return данные загруженного файла. Загруженные файлы доступны только вам
     */
    CompletableFuture<FileResponse> uploadFile(UploadFileRequest request);

    /**
     * Скачать файл
     *
     * @param fileId   Идентификатор изображения, полученный в ответ на запрос пользователя о генерации изображений
     * @param clientId идентификатор клиента
     * @return массив байт файла.
     */
    CompletableFuture<ByteArrayInputStream> downloadFile(String fileId, String clientId);

    /**
     * Получить список доступных файлов
     *
     * @return список с данными доступных файлов.
     */
    CompletableFuture<AvailableFilesResponse> getListAvailableFile();

    /**
     * Получить информацию о файле
     *
     * @param fileId идентификатор файла.
     * @return описание указанного файла.
     */
    CompletableFuture<FileResponse> getFileInfo(String fileId);

    /**
     * Удалить файл
     *
     * @param fileId идентификатор файла.
     * @return описание удаленного файла.
     */
    CompletableFuture<FileDeletedResponse> deleteFile(String fileId);

    /**
     * Подсчитать количество токенов
     *
     * @param request описание запроса на получение количества токенов
     * @return список с информацией о количестве токенов, подсчитанных заданной моделью в строках.
     */
    CompletableFuture<List<TokenCount>> tokensCount(TokenCountRequest request);

    /**
     * Получить остаток токенов
     *
     * @return возвращает доступный остаток токенов для каждой из моделей. Метод доступен только при покупке пакетов
     * токенов.
     */
    CompletableFuture<BalanceResponse> balance();

    static GigaChatClientAsyncImplBuilder builder() {
        return new GigaChatClientAsyncImplBuilder();
    }
}
