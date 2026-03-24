package chat.giga.http.client.sse;

/**
 * Обработчик потока по формату Server-Sent Events (поля {@code event:} и {@code data:}, разделитель — пустая строка).
 */
public interface SseEventListener {

    /**
     * @param eventType тип события из поля {@code event:}; {@code null}, если поле в блоке не было (анонимное событие)
     * @param data      объединённое тело полей {@code data:} (несколько строк через {@code \n})
     */
    void onEvent(String eventType, String data);

    /**
     * Поток успешно прочитан до конца, буфер последнего события сброшен.
     */
    void onClosed();

    void onError(Exception e);
}
