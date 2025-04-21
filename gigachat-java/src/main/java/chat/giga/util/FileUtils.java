package chat.giga.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

    public byte[] createMultiPartBody(byte[] fileBytes, String boundary, String purpose, String mimeType,
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
}
