package chat.giga.http.client.sse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SseParserTest {

    @Mock
    SseListener sseListener;

    @InjectMocks
    SseParser sseParser;

    @Test
    void parse() {
        sseParser.parse(new ByteArrayInputStream("""
                data: testData1
                
                data: testData2
                
                data: [DONE]
                
                data: testData3
                """.getBytes()));

        var captor = ArgumentCaptor.forClass(String.class);
        verify(sseListener, times(2)).onData(captor.capture());
        verify(sseListener).onComplete();

        assertThat(captor.getAllValues()).contains("testData1", "testData2");
    }

    @Test
    void parseFailed() throws IOException {
        var is = mock(InputStream.class);
        when(is.read(any(), anyInt(), anyInt())).thenThrow(new IOException());

        sseParser.parse(is);

        verify(sseListener).onError(any(UncheckedIOException.class));
        verify(sseListener, never()).onComplete();
    }
}
