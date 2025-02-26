package chat.giga.client;

import chat.giga.model.AccessTokenResponse;
import chat.giga.model.Scope;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.UUID;

public class GigaChatAuthClientImpl implements GigaChatAuthClient {


    private static final String AUTH_URL = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";

    @Override
    public AccessTokenResponse oauth(String clientId, String secret, Scope scope) {

        String formData = "scope=" + scope.name();

        String credentials = clientId + ":" + secret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        String rqUID = UUID.randomUUID().toString();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .header("RqUID", rqUID)
                .header("Authorization", "Basic " + encodedCredentials)
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            System.out.println("Code: " + response.statusCode());
            System.out.println("Response: " + jsonResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new AccessTokenResponse();
    }
}
