/**
 * DTO для HTTP API {@code v2/chat/completions}. Сериализация Jackson использует поля ({@code @JsonAutoDetect}), т.к. у
 * моделей включён Lombok {@code @Accessors(fluent = true)} без геттеров {@code get*}.
 */
package chat.giga.model.v2.completion;
