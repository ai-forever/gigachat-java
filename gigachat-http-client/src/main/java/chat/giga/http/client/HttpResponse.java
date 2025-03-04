package chat.giga.http.client;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.experimental.Accessors;

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

    public String bodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
