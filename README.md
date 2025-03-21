# GigaChat Java SDK

GigaChat — это Java-библиотека для работы с [REST API GigaChat](https://developers.sber.ru/docs/ru/gigachat/api/reference/rest/gigachat-api).

Библиотека управляет авторизацией запросов и предоставляет все необходимые методы для работы с API.
Кроме этого она поддерживает:

* [обработку потоковой передачи токенов](gigachat-java-example/src/main/java/chat/giga/CompletionStreamingExample.java);
* [работу с функциями](gigachat-java-example/src/main/java/chat/giga/FunctionExample.java);
* [создание эмбеддингов](gigachat-java-example/src/main/java/chat/giga/EmbeddingExample.java);
* работу в синхронном или в [асинхронном режиме](gigachat-java-example/src/main/java/chat/giga/CompletionStreamingExample.java).

> [!TIP]
> Больше примеров работы с библиотекой — в папке [gigachat-java-example](gigachat-java-example/README.md).

## Требования

Для работы библиотеки установите Java версии 17 или выше.

## Установка

Чтобы установить библиотеку, подключите ее в зависимости.

### Gradle

```kotlin
implementation("chat.giga:gigachat-java:0.1.2")
```

### Maven

```xml
<dependency>
    <groupId>chat.giga</groupId>
    <artifactId>gigachat-java</artifactId>
    <version>0.1.2</version>
</dependency>
```

## Быстрый старт

Для работы с библиотекой вам понадобится ключ авторизации API.

Чтобы получить ключ авторизации:

1. Создайте проект **GigaChat API** в личном кабинете Studio.
2. В интерфейсе проекта, в левой панели выберите раздел **Настройки API**.
3. Нажмите кнопку **Получить ключ**.

В открывшемся окне скопируйте и сохраните значение поля Authorization Key. Ключ авторизации, отображается только один раз и не хранятся в личном кабинете. При компрометации или утере ключа авторизации вы можете сгенерировать его повторно.

Подробно о том, как создать проект GigaChat API — в официальной документации, в разделах [Быстрый старт для физических лиц](https://developers.sber.ru/docs/ru/gigachat/individuals-quickstart) и [Быстрый старт для ИП и юридических лиц](https://developers.sber.ru/docs/ru/gigachat/legal-quickstart).

Передайте полученный ключ авторизации в параметре `authKey` при инициализации объекта AuthClient.

Пример показывает как отправить простой запрос на генерацию с помощью библиотеки GigaChatClient:

```java
//Укажите ключ авторизации, полученный в личном кабинете, в интерфейсе проекта GigaChat API
public class CompletionExample {

    public static void main(String[] args) {
        GigaChatClient client = GigaChatClient.builder()
                .verifySslCerts(false)
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey("ваш_ключ_авторизации")
                                .build())
                        .build())
                .build();

        System.out.println(client.completions(CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT_MAX)
                .message(ChatMessage.builder()
                        .content("Какие факторы влияют на стоимость страховки на дом?")
                        .role(Role.USER)
                        .build())
                .build()));
    }
}
```

> [!NOTE]
> Этот и другие примеры работы с библиотекой GigaChat — в папке [gigachat-java-example](gigachat-java-example/README.md).

## Параметры объекта GigaChatClient

Для работы с GigaChat используется класс GigaChatClient, который предоставляет доступ ко всем необходимым методам работы с API.

Подробнее о методах и параметрах класса — в [документации класса](gigachat-java/src/main/java/chat/giga/client/GigaChatClient.java).

## Способы аутентификации

Для аутентификации используется метод [`AuthClient.builder()`](gigachat-java/src/main/java/chat/giga/client/auth/AuthClient.java), который вызывается при создании экземпляра GigaChatClient и возвращает экземпляр [AuthClientBuilder](gigachat-java/src/main/java/chat/giga/client/auth/AuthClientBuilder.java)

Методы AuthClientBuilder позволяют выполнить аутентификацию с помощью:

* ключа авторизации;
* токена доступа;
* клиентского идентификатора (Client ID) и ключа (Client Secret);
* TLS-сертификата;
* имени пользователя и пароля.

### Аутентификация с помощью ключа авторизации

При аутентификации с помощью ключа авторизации в методе `scope()` нужно передать версию API, к которой будут выполняться запросы.

Возможные значения:

* `GIGACHAT_API_PERS` — версия API для физических лиц;
* `GIGACHAT_API_B2B` — версия API для ИП и юрлиц при работе по предоплате.
* `GIGACHAT_API_CORP` — версия API для ИП и юрлиц при работе по постоплате.

По умолчанию запросы передаются в версию для физических лиц.

```java
GigaChatClient client = GigaChatClient.builder()
        .authClient(AuthClient.builder()
                .withOAuth(OAuthBuilder.builder()
                        // Версия API
                        .scope(Scope.GIGACHAT_API_B2B)
                        .authKey("ключ_авторизации")
                        .build())
                .build())
        .build();
```

> [!TIP]
> Подробно о том, как создать проект GigaChat API — в официальной документации, в разделах [Быстрый старт для физических лиц](https://developers.sber.ru/docs/ru/gigachat/individuals-quickstart) и [Быстрый старт для ИП и юридических лиц](https://developers.sber.ru/docs/ru/gigachat/legal-quickstart).

### Аутентификация с помощью токена доступа

Токен доступа (access token) получается в обмен на ключ авторизации в запросе [`POST /api/v2/oauth`](https://developers.sber.ru/docs/ru/gigachat/api/reference/rest/post-token).

Токен действует в течение 30 минут и содержит данные о версии API, к которой предоставляется доступ, поэтому ее не нужно указывать дополнительно.

```java
GigaChatClient client = GigaChatClient.builder()
        .authClient(AuthClient.builder()
                .withProvidedTokenAuth("токен_доступа").build())
        .build();
```

### Аутентификация с помощью клиентского идентификатора и ключа

Как и при использовании ключа авторизации, при аутентификации с помощью Client ID и Client Secret нужно указывать версию API, к которой будут выполняться запросы.

```java
GigaChatClient client = GigaChatClient.builder()
        .authClient(AuthClient.builder()
                .withOAuth(OAuthBuilder.builder()
                        .scope(Scope.GIGACHAT_API_B2B)
                        .clientId("test-client-id")
                        .clientSecret("test-scope")
                        .build())
                .build())
        .logRequests(true)
        .logResponses(true)
        .build();
```

### Аутентификация с помощью имени пользователя и пароля

```java
GigaChatClient client = GigaChatClient.builder()
        .authClient(AuthClient.builder()
                .withUserPassword(
                        UserPasswordAuthBuilder.builder()
                                .user("user")
                                .password("password")
                                .authApiUrl("https://api.ru/v1")
                                .scope(Scope.GIGACHAT_API_PERS)
                                .build()).build()
        )
        .build();
```

### Аутентификация с помощью TLS-сертификата

```java
GigaChatClient client = GigaChatClient.builder()
        .authClient(AuthClient.builder()
                .withCertificatesAuth(new JdkHttpClientBuilder()
                        .httpClientBuilder(HttpClient.newBuilder())
                        .ssl(SSL.builder()
                                .truststorePassword("password")
                                .trustStoreType("PKCS12")
                                .truststorePath("/Users/test/ssl/client_truststore.p12")
                                .keystorePassword("password")
                                .keystoreType("PKCS12")
                                .keystorePath("/Users/test/ssl/client_keystore.p12")
                                .build())
                        .build())
                .build())
        .build();
```

## Установка корневого сертификата НУЦ Минцифры

Чтобы библиотека GigaChat могла передавать запросы в GigaChat API, вам нужно установить корневой сертификат [НУЦ Минцифры](https://developers.sber.ru/docs/ru/gigachat/certificates).

Для этого перейдите в папку `JAVA_HOME/bin` и выполните в консоли, запущенной от имени администратора, команду:

```shell
keytool -importcert -storepass changeit -noprompt -alias rus_root_ca -cacerts -trustcacerts -file /<путь_к_файлу_сертификата>/russian_trusted_root_ca_pem.crt
```

При необходимости вы можете отключить проверку сертификатов.
Для этого, создайте экземпляр GigaChatClient, с параметром `verifySslCerts(false)`:

```java
GigaChatClient client = GigaChatClient.builder()
        // Отключение проверки сертификатов
        .verifySslCerts(false)
        .authClient(AuthClient.builder()
                .withOAuth(OAuthBuilder.builder()
                        .scope(Scope.GIGACHAT_API_PERS)
                        .authKey("ключ_авторизации")
                        .build())
                .build())
        .build();
```

> [!WARNING]
> Отключение проверки сертификатов снижает безопасность обмена данными.
