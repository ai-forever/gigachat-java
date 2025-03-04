package chat.giga.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@UtilityClass
public class FileUtils {

    public static StringBuilder createMultiPartBody(File file, String boundary, String purpose, String contentType)
            throws IOException {
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("--").append(boundary).append("\r\n");
        requestBody.append("Content-Disposition: form-data; name=\"purpose\"\r\n\r\n");
        requestBody.append(purpose).append("\r\n");
        requestBody.append("--").append(boundary).append("\r\n");
        requestBody.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
        requestBody.append("Content-Type: ").append(contentType).append("\r\n\r\n");
        requestBody.append(new String(Files.readAllBytes(file.toPath()))).append("\r\n");
        requestBody.append("--").append(boundary).append("--").append("\r\n");
        return requestBody;
    }
}
