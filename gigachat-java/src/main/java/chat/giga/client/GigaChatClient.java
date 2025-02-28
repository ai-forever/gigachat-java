package chat.giga.client;

import chat.giga.client.GigaChatClientImpl.GigaChatClientImplBuilder;
import chat.giga.model.DownloadFileRequest;
import chat.giga.model.DownloadFileResponse;
import chat.giga.model.TokenCountRequest;
import chat.giga.model.UploadFileRequest;
import chat.giga.model.UploadFileResponse;
import chat.giga.model.BalanceResponse;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.embedding.EmbeddingRequest;
import chat.giga.model.embedding.EmbeddingResponse;
import chat.giga.model.ModelResponse;
import chat.giga.model.TokenCount;

import java.util.List;

public interface GigaChatClient {

    /**
     * Получить список моделей
     *
     * @return массив объектов с данными доступных моделей.
     */
    ModelResponse models();

    /**
     * Получить ответ модели на сообщения
     *
     * @param request описание запроса на получение ответа от модели
     * @return ответ модели, сгенерированный на основе переданных сообщений.
     */
    CompletionResponse completions(CompletionRequest request);

    /**
     * Создать эмбендиг
     *
     * @param request описание запроса на получения эмбендинга
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
     * @return объект с данными загруженного файла. Загруженные файлы доступны только вам
     */
    UploadFileResponse uploadFile(UploadFileRequest request);

    /**
     * Скачать файл
     *
     * @param request описание запроса на скачивание файла
     * @return массив объектов с данными доступных файлов.
     */
    DownloadFileResponse downloadFile(DownloadFileRequest request);

    /**
     * Подсчитать количество токенов
     *
     * @param request описание запроса на получение количества токенов
     * @return объект с информацией о количестве токенов, подсчитанных заданной моделью в строках. Строки передаются в
     * массиве input.
     */
    List<TokenCount> tokensCount(TokenCountRequest request);

    /**
     * Получить остаток токенов
     *
     * @return Возвращает доступный остаток токенов для каждой из моделей. Метод доступен только при покупке пакетов
     * токенов.
     */
    BalanceResponse balance();

    static GigaChatClientImpl.GigaChatClientImplBuilder builder() {
        return new GigaChatClientImplBuilder();
    }
}
