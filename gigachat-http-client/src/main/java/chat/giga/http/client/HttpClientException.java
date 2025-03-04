package chat.giga.http.client;


import java.nio.charset.StandardCharsets;

public class HttpClientException extends RuntimeException {

    private final int statusCode;
    private final byte[] body;

    public HttpClientException(int statusCode, byte[] body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int statusCode() {
        return statusCode;
    }


    public byte[] body() {
        return body;
    }

    public String bodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
