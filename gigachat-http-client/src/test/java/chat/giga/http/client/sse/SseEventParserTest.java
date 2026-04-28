package chat.giga.http.client.sse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SseEventParserTest {

    @Mock
    SseEventListener sseEventListener;

    @Test
    void parseNamedEventsWithBlankLineDelimiter() {
        var parser = new SseEventParser(sseEventListener);
        parser.parse(new ByteArrayInputStream("""
                event: response.message.delta
                data: {"x":1}

                event: response.message.done
                data: {"finish_reason":"stop"}

                """.getBytes()));

        var typeCaptor = ArgumentCaptor.forClass(String.class);
        var dataCaptor = ArgumentCaptor.forClass(String.class);
        verify(sseEventListener, times(2)).onEvent(typeCaptor.capture(), dataCaptor.capture());
        verify(sseEventListener).onClosed();
        verify(sseEventListener, never()).onError(any());

        assertThat(typeCaptor.getAllValues()).containsExactly("response.message.delta", "response.message.done");
        assertThat(dataCaptor.getAllValues()).containsExactly("{\"x\":1}", "{\"finish_reason\":\"stop\"}");
    }

    @Test
    void parseMultilineData() {
        var parser = new SseEventParser(sseEventListener);
        parser.parse(new ByteArrayInputStream("""
                event: test
                data: line1
                data: line2

                """.getBytes()));

        verify(sseEventListener).onEvent("test", "line1\nline2");
        verify(sseEventListener).onClosed();
    }

    @Test
    void parseAnonymousEvent() {
        var parser = new SseEventParser(sseEventListener);
        parser.parse(new ByteArrayInputStream("""
                data: only-data

                """.getBytes()));

        verify(sseEventListener).onEvent(null, "only-data");
        verify(sseEventListener).onClosed();
    }

    @Test
    void parseFlushesLastBlockWithoutTrailingBlank() {
        var parser = new SseEventParser(sseEventListener);
        parser.parse(new ByteArrayInputStream("""
                event: response.message.done
                data: {}""".getBytes()));

        verify(sseEventListener).onEvent("response.message.done", "{}");
        verify(sseEventListener).onClosed();
    }

    @Test
    void parseSkipsComments() {
        var parser = new SseEventParser(sseEventListener);
        parser.parse(new ByteArrayInputStream("""
                : comment
                data: ok

                """.getBytes()));

        verify(sseEventListener).onEvent(null, "ok");
        verify(sseEventListener).onClosed();
    }

    @Test
    void parseFailed() throws IOException {
        var is = mock(InputStream.class);
        when(is.read(any(), anyInt(), anyInt())).thenThrow(new IOException());

        var parser = new SseEventParser(sseEventListener);
        parser.parse(is);

        verify(sseEventListener).onError(any(IOException.class));
        verify(sseEventListener, never()).onClosed();
    }
}
