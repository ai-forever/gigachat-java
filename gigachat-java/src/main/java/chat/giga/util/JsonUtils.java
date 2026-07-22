package chat.giga.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .defaultPropertyInclusion(
                    JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.USE_DEFAULTS))
            .build();

    private JsonUtils() {
    }

    public static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }
}
