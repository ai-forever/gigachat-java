package chat.giga.http.client;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import lombok.experimental.Accessors;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public String bodyAsString() throws IOException {
        return IOUtils.toString(body, StandardCharsets.UTF_8);
    }

}
