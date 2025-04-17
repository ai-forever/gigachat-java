package chat.giga.http.client;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

public class JdkHttpClientBuilder implements HttpClientBuilder {

    private java.net.http.HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
    private Duration connectTimeout;
    private Duration readTimeout;
    private SSL ssl;
    private Map<String, String> customHeaders;

    public java.net.http.HttpClient.Builder httpClientBuilder() {
        return httpClientBuilder;
    }

    public JdkHttpClientBuilder httpClientBuilder(java.net.http.HttpClient.Builder httpClientBuilder) {
        this.httpClientBuilder = httpClientBuilder;
        return this;
    }

    @Override
    public Duration connectTimeout() {
        return connectTimeout;
    }

    @Override
    public JdkHttpClientBuilder connectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @Override
    public Duration readTimeout() {
        return readTimeout;
    }

    @Override
    public JdkHttpClientBuilder readTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    @Override
    public JdkHttpClient build() {
        return new JdkHttpClient(this);
    }

    @Override
    public SSL ssl() {
        return this.ssl;
    }

    @Override
    public HttpClientBuilder ssl(SSL ssl) {
        this.ssl = ssl;
        return this;
    }

    @Override
    public Map<String, String> customHeaders() {
        return customHeaders;
    }

    @Override
    public HttpClientBuilder customHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
        return this;
    }
}
