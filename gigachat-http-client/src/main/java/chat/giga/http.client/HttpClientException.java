package chat.giga.http.client;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpClientException extends RuntimeException {

    private final int statusCode;
    private final InputStream body;

    public HttpClientException(int statusCode, InputStream body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int statusCode() {
        return statusCode;
    }


    public InputStream body() {
        return body;
    }

    public String bodyAsString() {
        try {
            return IOUtils.toString(body, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}
