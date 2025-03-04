package chat.giga.http.client.sse;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@RequiredArgsConstructor
public class SseParser {

    private final SseListener listener;

    public void parse(InputStream body) {
        try (var reader = new BufferedReader(new InputStreamReader(body))) {

            reader.lines()
                    .forEach(l -> {
                        var data = l.split(": ");
                        if ("data".equals(data[0])) {
                            if ("[DONE]".equals(data[1])) {
                                return;
                            }

                            listener.onData(data[1]);
                        }
                    });
        } catch (Exception e) {
            listener.onError(e);
        }
    }
}
