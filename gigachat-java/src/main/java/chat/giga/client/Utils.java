package chat.giga.client;

public class Utils {

    public static <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
