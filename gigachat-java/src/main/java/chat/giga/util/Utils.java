package chat.giga.util;


public final class Utils {

    private Utils() {
    }

    public static <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
