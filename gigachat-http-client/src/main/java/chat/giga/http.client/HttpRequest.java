package chat.giga.http.client;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Value
@Builder
@Accessors(fluent = true)
public class HttpRequest {

    HttpMethod method;
    String url;
    Map<String, List<String>> headers;
    InputStream body;

    public static class HttpRequestBuilder {

        Map<String, List<String>> headers = new ConcurrentHashMap<>();

        public HttpRequestBuilder header(String name, String value) {
            this.headers.computeIfAbsent(name, k -> new ArrayList<>(1))
                    .add(value);
            return this;
        }
    }
}
