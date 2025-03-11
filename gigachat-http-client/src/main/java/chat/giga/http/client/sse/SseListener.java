package chat.giga.http.client.sse;

public interface SseListener {

    void onData(String data);

    void onComplete();

    void onError(Exception ex);
}
