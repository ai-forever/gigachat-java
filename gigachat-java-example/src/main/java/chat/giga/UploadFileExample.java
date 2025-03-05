package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.Scope;
import chat.giga.model.file.UploadFileRequest;

public class UploadFileExample {

    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .clientId("test-client-id")
                .clientSecret("test-scope")
                .scope(Scope.GIGACHAT_API_PERS)
                .build();
        try {
            System.out.println(client.uploadFile(UploadFileRequest.builder()
                    .file(new byte[1000])
                    .mimeType("application/pdf")
                    .fileName("test.pdf")
                    .purpose("general")
                    .build()));
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
