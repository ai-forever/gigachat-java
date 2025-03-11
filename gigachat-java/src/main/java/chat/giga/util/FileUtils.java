package chat.giga.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

    public StringBuilder createMultiPartBody(byte[] file, String boundary, String purpose, String contentType,
            String fileName) {
        var requestBody = new StringBuilder();
        requestBody.append("--").append(boundary).append("\r\n");
        requestBody.append("Content-Disposition: form-data; name=\"purpose\"\r\n\r\n");
        requestBody.append(purpose).append("\r\n");
        requestBody.append("--").append(boundary).append("\r\n");
        requestBody.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName)
                .append("\"\r\n");
        requestBody.append("Content-Type: ").append(contentType).append("\r\n\r\n");
        requestBody.append(new String(file)).append("\r\n");
        requestBody.append("--").append(boundary).append("--").append("\r\n");

        return requestBody;
    }
}
