package chat.giga.http.client;


import java.nio.charset.StandardCharsets;

public class HttpClientException extends RuntimeException {

    private final int statusCode;
    private final byte[] body;

    public HttpClientException(int statusCode, byte[] body) {
        super(String.format("Client error, status: %s, body: %s", statusCode,
                (body != null && body.length > 0) ? new String(body, StandardCharsets.UTF_8) : null));
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
        if (body != null && body.length > 0) {
            return new String(body, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
}
