package chat.giga.http.client;

import java.io.InputStream;

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
}
