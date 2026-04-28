package chat.giga.http.client.sse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсер SSE с поддержкой полей {@code event:} и многострочного {@code data:} (как в API v2 GigaChat).
 */
@RequiredArgsConstructor
public class SseEventParser {

    private static final Logger log = LoggerFactory.getLogger(SseEventParser.class);
    private static final int MAX_RAW_LINE_LOG_CHARS = 2048;

    private final SseEventListener listener;

    public void parse(InputStream body) {
        try (var reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {
            List<String> dataLines = new ArrayList<>();
            String eventType = null;
            boolean pending = false;

            String line;
            while ((line = reader.readLine()) != null) {
                if (log.isTraceEnabled()) {
                    log.trace("SSE raw line: {}", truncateForLog(line));
                }
                if (line.isEmpty()) {
                    dispatch(dataLines, eventType, pending);
                    dataLines = new ArrayList<>();
                    eventType = null;
                    pending = false;
                    continue;
                }
                if (line.startsWith(":")) {
                    continue;
                }
                int colon = line.indexOf(':');
                if (colon < 0) {
                    continue;
                }
                String field = line.substring(0, colon);
                String value = colon + 1 < line.length() ? line.substring(colon + 1) : "";
                if (value.startsWith(" ")) {
                    value = value.substring(1);
                }
                switch (field) {
                    case "event" -> {
                        eventType = value;
                        pending = true;
                    }
                    case "data" -> {
                        dataLines.add(value);
                        pending = true;
                    }
                    default -> {
                        // id, retry и прочие поля пока игнорируем
                    }
                }
            }
            dispatch(dataLines, eventType, pending);
            listener.onClosed();
        } catch (Exception e) {
            listener.onError(e);
        }
    }

    private void dispatch(List<String> dataLines, String eventType, boolean pending) {
        if (!pending) {
            return;
        }
        listener.onEvent(eventType, String.join("\n", dataLines));
    }

    private static String truncateForLog(String line) {
        if (line == null || line.length() <= MAX_RAW_LINE_LOG_CHARS) {
            return line;
        }
        return line.substring(0, MAX_RAW_LINE_LOG_CHARS) + "…(" + line.length() + " chars)";
    }
}
