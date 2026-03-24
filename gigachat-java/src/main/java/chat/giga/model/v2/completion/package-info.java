/**
 * DTO для HTTP API {@code v2/chat/completions}. Сериализация Jackson использует поля ({@code @JsonAutoDetect}), т.к. у
 * моделей включён Lombok {@code @Accessors(fluent = true)} без геттеров {@code get*}. Описания полей выверены по
 * экспорту Confluence релиза WMapi (раздел POST {@code v2/chat/completions}).
 */
package chat.giga.model.v2.completion;
