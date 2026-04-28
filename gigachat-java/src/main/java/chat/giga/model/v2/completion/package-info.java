/**
 * DTO для HTTP API {@code v2/chat/completions}. У полей заданы {@code @JsonProperty} (имена JSON в snake_case, где
 * нужно), чтобы Jackson корректно работал вместе с Lombok {@code @Accessors(fluent = true)} без классических {@code get*}-геттеров.
 */
package chat.giga.model.v2.completion;
