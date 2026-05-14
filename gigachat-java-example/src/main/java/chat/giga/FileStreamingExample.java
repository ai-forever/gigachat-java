package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.Scope;
import chat.giga.model.file.FileResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Пример использования стриминговых методов uploadFile(InputStream, ...) и downloadFileAsStream(...), которые не
 * загружают файл целиком в память. Полезно для больших файлов (сотни МБ).
 */
public class FileStreamingExample {

    public static void main(String[] args) {

        try (GigaChatClient client = GigaChatClient.builder()
                .verifySslCerts(false)
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId("your-client-id")
                                .clientSecret("your-client-secret")
                                .build())
                        .build())
                .build()) {

            String fileName = "hello.txt";
            String mimeType = "text/plain";
            String fileContent = "Привет, мир! Это стриминговая загрузка.";
            InputStream fileStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

            FileResponse uploaded = client.uploadFileAsStream("general", fileStream, mimeType, fileName);
            System.out.println("Загружен файл: " + uploaded.id());

            // Скачиваем обратно через стрим
            try (InputStream downloaded = client.downloadFileAsStream(uploaded.id().toString(), null)) {
                String content = new String(downloaded.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Скачан файл, содержимое: " + content);
            }

            // Удаляем
            client.deleteFile(uploaded.id().toString());
            System.out.println("Файл удалён.");

        } catch (HttpClientException ex) {
            System.out.println("HTTP ошибка: " + ex.statusCode() + " " + ex.bodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
