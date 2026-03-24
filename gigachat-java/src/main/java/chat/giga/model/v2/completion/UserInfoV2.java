package chat.giga.model.v2.completion;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * Объект {@code user_info}: сведения о клиенте для модели (улучшение ответов, согласование с тулом
 * {@code get_datetime}).
 */
@Value
@Builder
@Jacksonized
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class UserInfoV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Часовой пояс в нотации IANA (например {@code Europe/Moscow}).
     */
    @JsonProperty
    String timezone;
}
