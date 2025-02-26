package chat.giga;

import chat.giga.client.GigaChatAuthClient;
import chat.giga.client.GigaChatAuthClientImpl;
import chat.giga.model.Scope;

public class Main {
    public static void main(String[] args) {

//        GigaChatClient client = ClientBuilder.token().clientId().secret().scope().build();
//        client.embeddings()

        GigaChatAuthClient client = new GigaChatAuthClientImpl();
        client.oauth("680f53b4-936b-4e10-ac12-427354dbd90c", "6ccd7d51-39ee-443f-8412-b8438114583c", Scope.GIGACHAT_API_PERS);


    }
}