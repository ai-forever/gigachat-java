package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.model.Scope;
import chat.giga.model.file.UploadFileRequest;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        GigaChatClient client = GigaChatClient.builder()
                .clientId("680f53b4-936b-4e10-ac12-427354dbd90c")
                .clientSecret("6ccd7d51-39ee-443f-8412-b8438114583c")
                .scope(Scope.GIGACHAT_API_PERS)
                .build();

//        client.models();
        client.uploadFile(UploadFileRequest.builder().purpose("general").file(new File("/Users/19142944/Documents/gigachat-java-sdk/gen/Задача.pdf")).build());
    }
}
