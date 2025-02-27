package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.GigaChatDefaultClient;
import chat.giga.model.Scope;

public class Main {
    public static void main(String[] args) {

        GigaChatClient client = GigaChatDefaultClient.builder()
                .clientId("680f53b4-936b-4e10-ac12-427354dbd90c")
                .clientSecret("6ccd7d51-39ee-443f-8412-b8438114583c")
                .scope(Scope.GIGACHAT_API_PERS)
                .build();

        client.models();

        client.models();

        client.models();



    }
}
