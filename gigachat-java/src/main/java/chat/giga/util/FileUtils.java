package chat.giga.util;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;

public final class FileUtils {

    private FileUtils() {
    }

    public static byte[] createMultiPartBody(byte[] fileBytes, String boundary, String purpose, String mimeType,
                                      String fileName) {

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n")
                .append("Content-Type: ").append(mimeType).append("\r\n\r\n");

        byte[] bodyStart = bodyBuilder.toString().getBytes();
        bodyBuilder = new StringBuilder();
        bodyBuilder.append("\r\n--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"purpose\"\r\n\r\n")
                .append(purpose).append("\r\n")
                .append("--").append(boundary).append("--\r\n");
        byte[] bodyEnd = bodyBuilder.toString().getBytes();

        byte[] multipartBody = new byte[bodyStart.length + fileBytes.length + bodyEnd.length];
        System.arraycopy(bodyStart, 0, multipartBody, 0, bodyStart.length);
        System.arraycopy(fileBytes, 0, multipartBody, bodyStart.length, fileBytes.length);
        System.arraycopy(bodyEnd, 0, multipartBody, bodyStart.length + fileBytes.length, bodyEnd.length);

        return multipartBody;
    }

    /**
     * Создать multipart/form-data тело как InputStream, не загружая файл целиком в память. Поток собирается из трёх
     * частей через SequenceInputStream: заголовок + файл + завершение. Вызывающая сторона ОБЯЗАНА закрыть возвращённый
     * InputStream.
     */
    public InputStream createMultiPartBodyAsStream(InputStream fileStream, String boundary, String purpose,
            String mimeType, String fileName) {
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n")
                .append("Content-Type: ").append(mimeType).append("\r\n\r\n");

        byte[] bodyStart = bodyBuilder.toString().getBytes(StandardCharsets.UTF_8);

        bodyBuilder = new StringBuilder();
        bodyBuilder.append("\r\n--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"purpose\"\r\n\r\n")
                .append(purpose).append("\r\n")
                .append("--").append(boundary).append("--\r\n");
        byte[] bodyEnd = bodyBuilder.toString().getBytes(StandardCharsets.UTF_8);

        return new SequenceInputStream(
                new ByteArrayInputStream(bodyStart),
                new SequenceInputStream(fileStream, new ByteArrayInputStream(bodyEnd))
        );
    }
}
