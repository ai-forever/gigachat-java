package chat.giga.client.auth;

import chat.giga.http.client.HttpClient;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.model.Scope;
import chat.giga.util.Utils;
import lombok.Builder;


import static java.time.Duration.ofSeconds;

public class AuthClientBuilder {

    private AuthClient method;

    public static AuthClientBuilder builder() {
        return new AuthClientBuilder();
    }

    public AuthClientBuilder withOAuth(OAuthBuilder builder) {
        this.method = new OAuthClient(builder.httpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(Utils.getOrDefault(builder.readTimeout, 15)))
                .connectTimeout(ofSeconds(Utils.getOrDefault(builder.connectTimeout, 15)))
                .build() : builder.httpClient, builder.clientId, builder.clientSecret, builder.authKey, builder.scope,
                builder.authApiUrl);
        return this;
    }

    public AuthClientBuilder withUserPassword(UserPasswordAuthBuilder builder) {
        this.method = new UserPasswordAuthClient(builder.httpClient == null ? new JdkHttpClientBuilder()
                .readTimeout(ofSeconds(Utils.getOrDefault(builder.readTimeout, 15)))
                .connectTimeout(ofSeconds(Utils.getOrDefault(builder.connectTimeout, 15)))
                .build() : builder.httpClient, builder.user, builder.password, builder.scope,
                builder.authApiUrl);
        return this;
    }

    public AuthClientBuilder withProvidedTokenAuth(String accessToken) {
        this.method = new ProvidedTokenAuthClient(accessToken);
        return this;
    }

    public AuthClientBuilder withCertificatesAuth(HttpClient httpClient) {
        this.method = new CertificateAuthClient(httpClient);
        return this;
    }

    public AuthClient build() {
        if (method == null) {
            throw new IllegalStateException("Authentication method not specified");
        }
        return method;
    }

    @Builder
    public static class OAuthBuilder {

        private String clientId;
        private String clientSecret;
        private String authKey;
        private HttpClient httpClient;
        private Integer readTimeout;
        private Integer connectTimeout;
        private Scope scope;
        private String authApiUrl;
    }

    @Builder
    public static class UserPasswordAuthBuilder {

        private String user;
        private String password;
        private HttpClient httpClient;
        private Integer readTimeout;
        private Integer connectTimeout;
        private Scope scope;
        private String authApiUrl;
    }

}
