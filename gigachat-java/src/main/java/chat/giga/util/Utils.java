package chat.giga.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    public <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
