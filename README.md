GigaChat — это Java-библиотека для работы с [REST API GigaChat](https://developers.sber.ru/docs/ru/gigachat/api/reference/rest/gigachat-api).

## Установка

### Gradle

```kotlin
implementation("chat.giga:gigachat-java:0.1.0")
```

### Maven

```xml
<dependency>
    <groupId>chat.giga</groupId>
    <artifactId>gigachat-java</artifactId>
    <version>0.1.0</version>
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

Передайте полученный ключ авторизации в параметре `credentials` при инициализации объекта GigaChat.

Пример показывает как отправить простой запрос на генерацию с помощью библиотеки GigaChat:

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
> Этот и другие примеры работы с библиотекой gigachat — в папке [examples](gigachat-java-example/README.md).

## Параметры объекта GigaChatClient

## Способы авторизации


