package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.Scope;
import chat.giga.model.file.FileResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Пример использования стриминговых методов uploadFileAsStream(Supplier&lt;InputStream&gt;, ...) и
 * downloadFileAsStream(...), которые не загружают файл целиком в память. Полезно для больших файлов (сотни МБ).
 */
public class FileStreamingExample {

    public static void main(String[] args) {

        try (GigaChatClient client = GigaChatClient.builder()

                .verifySslCerts(false)
                .apiUrl("https://gigachat.sberdevices.ru/v1")
                .authClient(AuthClient.builder()
                        .withUserPassword(AuthClientBuilder.UserPasswordAuthBuilder.builder()
                                .authApiUrl("https://gigachat.sberdevices.ru/v1")
                                .scope(Scope.GIGACHAT_API_PERS)
                                .user(System.getenv("AUTH_USER"))
                                .password(System.getenv("AUTH_PASS"))
                                .build())
                        .build())
                .logResponses(true)
                .logRequests(true)
                .readTimeout(90)
                .build()) {

            String fileName = "hello.txt";
            String mimeType = "text/plain";
            String fileContent = "Привет, мир! Это стриминговая загрузка.";
            byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);

            FileResponse uploaded = client.uploadFileAsStream("general",
                    () -> new ByteArrayInputStream(fileBytes), mimeType, fileName);
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
