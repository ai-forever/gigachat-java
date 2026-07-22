package chat.giga.http.client;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
@Builder
@Accessors(fluent = true)
public class HttpResponse {

    int statusCode;
    @Default
    Map<String, List<String>> headers = new HashMap<>();
    byte[] body;
    InputStream bodyStream;

    /**
     * Тело ответа как поток. Если {@code bodyStream} не задан, но есть {@code body}, возвращает
     * {@link ByteArrayInputStream} над ним.
     */
    public InputStream bodyAsStream() {
        if (bodyStream != null) {
            return bodyStream;
        }
        if (body != null) {
            return new ByteArrayInputStream(body);
        }
        return null;
    }

    public String bodyAsString() {
        if (body != null && body.length > 0) {
            return new String(body, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
}
