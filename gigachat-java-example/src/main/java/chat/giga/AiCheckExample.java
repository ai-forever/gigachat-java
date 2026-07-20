package chat.giga;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder.OAuthBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.AiCheckRequest;
import chat.giga.model.AiCheckResponse;
import chat.giga.model.Scope;

public class AiCheckExample {

    public static void main(String[] args) {

        try (GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId("your-client-id")
                                .clientSecret("your-client-secret")
                                .build())
                        .build())
                .build()) {

            AiCheckRequest request = AiCheckRequest.builder()
                    .model("GigaCheckClassification")
                    .input("Первый искусственный спутник Земли был запущен Советским Союзом 4 октября 1957 года. "
                            + "Этот исторический запуск ознаменовал начало космической эры "
                            + "и стал важным событием в истории человечества.")
                    .build();

            AiCheckResponse response = client.aiCheck(request);

            System.out.println("Category: " + response.category());
            System.out.println("Characters: " + response.characters());
            System.out.println("Tokens: " + response.tokens());
            System.out.println("AI intervals: " + response.aiIntervals());

        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
