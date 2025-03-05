package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.Scope;

import java.util.UUID;

public class FileDeletedExample {

    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .clientId("test-client-id")
                .clientSecret("test-scope")
                .scope(Scope.GIGACHAT_API_PERS)
                .build();
        try {
            System.out.println(client.deleteFile(UUID.randomUUID().toString()));
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }
}
