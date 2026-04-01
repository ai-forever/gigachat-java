package chat.giga.client;

import chat.giga.model.v2.completion.stream.CompletionMessageDeltaEventV2;
import chat.giga.model.v2.completion.stream.CompletionMessageDoneEventV2;
import chat.giga.model.v2.completion.stream.CompletionToolLifecycleEventV2;

/**
 * Обработчик потока {@code POST /v2/chat/completions} с верхнеуровневым {@code stream: true}.
 */
public interface CompletionV2StreamHandler {

    void onMessageDelta(CompletionMessageDeltaEventV2 event);

    void onMessageDone(CompletionMessageDoneEventV2 event);

    /**
     * Событие {@code response.tool.in_progress} (платформенные тулы).
     */
    default void onToolInProgress(CompletionToolLifecycleEventV2 event) {
    }

    /**
     * Событие {@code response.tool.completed}.
     */
    default void onToolCompleted(CompletionToolLifecycleEventV2 event) {
    }

    /**
     * Вызывается после успешного получения {@code response.message.done} и закрытия HTTP-потока.
     */
    void onComplete();

    void onError(Throwable th);
}
