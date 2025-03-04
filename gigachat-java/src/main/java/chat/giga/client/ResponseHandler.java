package chat.giga.client;

public interface ResponseHandler<T> {

    void onNext(T chunk);

    void onComplete();

    void onError(Throwable th);
}
