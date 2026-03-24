package chat.giga.model.v2.completion.stream;

/**
 * Имена SSE-событий потокового ответа API v2 (вместо {@code data: [DONE]} в v1). См. раздел SSE в документации релиза
 * WMapi.
 */
public final class CompletionV2SseEvents {

    public static final String RESPONSE_MESSAGE_DELTA = "response.message.delta";
    /**
     * Завершение генерации: {@code finish_reason}, {@code tools_state_id}, usage и пр.
     */
    public static final String RESPONSE_MESSAGE_DONE = "response.message.done";
    /**
     * Промежуточное состояние платформенной тулы (reasoning / tool_execution).
     */
    public static final String RESPONSE_TOOL_IN_PROGRESS = "response.tool.in_progress";
    /**
     * Завершение платформенной тулы ({@code status} success/fail и т.д.).
     */
    public static final String RESPONSE_TOOL_COMPLETED = "response.tool.completed";

    private CompletionV2SseEvents() {
    }
}
