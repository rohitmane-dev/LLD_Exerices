package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy");

    public static String getCurrentTime() {
        return LocalDateTime.now().format(FORMAT);
    }
}
