package chat.giga.http.client;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Value
@Builder
@Accessors(fluent = true)
public class HttpResponse {

    int statusCode;
    @Default
    Map<String, List<String>> headers = Map.of();
    InputStream body;
}
