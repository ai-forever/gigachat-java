package chat.giga.client;

import chat.giga.client.GigaChatClientImpl.GigaChatClientImplBuilder;
import chat.giga.model.BalanceResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.TokenCount;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.batch.BatchCreateResponse;
import chat.giga.model.batch.BatchItem;
import chat.giga.model.batch.BatchMethod;
import chat.giga.model.batch.BatchRequest;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.model.file.AvailableFilesResponse;
import chat.giga.model.file.FileDeletedResponse;
import chat.giga.model.file.FileResponse;
import chat.giga.model.file.UploadFileRequest;
import chat.giga.model.filter.FilterCheckRequest;
import chat.giga.model.filter.FilterCheckResponse;

import java.util.List;

public interface GigaChatClient {

    /**
     * Получить список моделей
     *
     * @return список данных доступных моделей.
     */
    ModelResponse models();

    /**
     * Получить ответ модели на сообщения
     *
     * @param request описание запроса на получение ответа от модели
     * @return ответ модели, сгенерированный на основе переданных сообщений.
     */
    default CompletionResponse completions(CompletionRequest request) {
        return completions(request, null);
    }


    /**
     * Получить ответ модели на сообщения
     *
     * @param request описание запроса на получение ответа от модели
     * @param sessionId для кэширования контекста разговора с GigaChat. Идентификатор передается в заголовке запроса и может содержать произвольную строку.
     *                  Если при получении запроса, модель находит в кэше данные о запросе с таким же идентификатором и частично совпадающим контекстом, то она не
     *                  пересчитывает этот контекст
     * @return ответ модели, сгенерированный на основе переданных сообщений.
     */
    CompletionResponse completions(CompletionRequest request, String sessionId);

    /**
     * Создать эмбеддинг
     *
     * @param request описание запроса на получения эмбеддинга
     * @return векторные представления соответствующих текстовых запросов. Векторное представление выглядит как массив
     * чисел `embedding`. Каждое значение в массиве представляет одну из характеристик или признаков текста, учтенных
     * при вычислении эмбеддинга. Значения образуют числовое представление текста и позволяют анализировать и
     * использовать текст в различных задачах.
     */
    EmbeddingResponse embeddings(EmbeddingRequest request);

    /**
     * Загрузить файл
     *
     * @param request описание запроса запрос на загрузку файла
     * @return данные загруженного файла. Загруженные файлы доступны только вам
     */
    FileResponse uploadFile(UploadFileRequest request);

    /**
     * Скачать файл
     *
     * @param fileId   Идентификатор изображения, полученный в ответ на запрос пользователя о генерации изображений
     * @param clientId идентификатор клиента
     * @return массив байт файла.
     */
    byte[] downloadFile(String fileId, String clientId);

    /**
     * Получить список доступных файлов
     *
     * @return список с данными доступных файлов.
     */
    AvailableFilesResponse availableFileList();

    /**
     * Получить информацию о файле
     *
     * @param fileId идентификатор файла.
     * @return описание указанного файла.
     */
    FileResponse fileInfo(String fileId);

    /**
     * Удалить файл
     *
     * @param fileId идентификатор файла.
     * @return описание удаленного файла.
     */
    FileDeletedResponse deleteFile(String fileId);

    /**
     * Подсчитать количество токенов
     *
     * @param request описание запроса на получение количества токенов
     * @return список с информацией о количестве токенов, подсчитанных заданной моделью в строках.
     */
    List<TokenCount> tokensCount(TokenCountRequest request);

    /**
     * Получить остаток токенов
     *
     * @return возвращает доступный остаток токенов для каждой из моделей. Метод доступен только при покупке пакетов
     * токенов.
     */
    BalanceResponse balance();

    /**
     * Создать пакет запросов (batch).
     *
     * @param jsonlRequest тело запроса в формате JSONL
     * @param method       метод обработки запросов: {@link BatchMethod#CHAT_COMPLETIONS} или {@link BatchMethod#EMBEDDER}
     * @return ответ с идентификатором, методом, количеством запросов, статусом и временными метками созданной пакетной задачи
     */
    BatchCreateResponse createBatch(byte[] jsonlRequest, BatchMethod method);

    /**
     * Создать пакет запросов (batch) из списка типизированных задач. Задачи автоматически сериализуются в формат
     * JSONL.
     *
     * @param requests список задач {@link BatchRequest}, каждая содержит id и тело запроса
     * @param method   метод обработки запросов: {@link BatchMethod#CHAT_COMPLETIONS} или {@link BatchMethod#EMBEDDER}
     * @return ответ с идентификатором, методом, количеством запросов, статусом и временными метками созданной пакетной
     * задачи
     */
    default BatchCreateResponse createBatch(List<BatchRequest> requests, BatchMethod method) {
        return createBatch(BatchRequest.toJsonl(requests), method);
    }

    /**
     * Получить список пакетных задач.
     *
     * @param batchId идентификатор пакетной задачи
     * @return список пакетных задач, каждый элемент содержит: id, method, request_counts, status, output_file_id (при
     * completed), created_at, updated_at
     */
    List<BatchItem> batchStatus(String batchId);

    /**
     * Проверить текст на наличие ненормативной лексики и других нежелательных элементов.
     *
     * @param request описание запроса на проверку текста
     * @return ответ с признаком наличия ненормативной лексики и информацией об использовании токенов.
     */
    FilterCheckResponse filterCheck(FilterCheckRequest request);

    static GigaChatClientImplBuilder builder() {
        return new GigaChatClientImplBuilder();
    }
}
