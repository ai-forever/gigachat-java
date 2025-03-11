package chat.giga.util;

import chat.giga.http.client.HttpClientException;
import lombok.experimental.UtilityClass;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class RetryUtils {

    public <T> T retry401(Supplier<T> supplier, int maxRetries) {
        int retries = 0;
        while (retries <= maxRetries) {
            try {
                return supplier.get();
            } catch (HttpClientException e) {
                if (e.statusCode() == 401) {
                    retries++;
                } else {
                    throw e;
                }
            }
        }
        throw new IllegalStateException(String.format("Retries exhausted after %s attempts", maxRetries));
    }

    public <T> CompletableFuture<T> retry401Async(Supplier<CompletableFuture<T>> supplier, int maxRetries) {
        var future = supplier.get();
        for (int i = 0; i <= maxRetries; i++) {
            future = future.handleAsync((r, th) -> {
                if (th != null) {
                    if (th.getCause() instanceof HttpClientException e && e.statusCode() == 401) {
                        return supplier.get();
                    } else {
                        return CompletableFuture.<T>failedFuture(th);
                    }
                }
                return CompletableFuture.completedFuture(r);
            }).thenCompose(Function.identity());
        }

        return future;
    }
}
