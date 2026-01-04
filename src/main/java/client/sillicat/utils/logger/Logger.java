package client.sillicat.utils.logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static void log(String message){
        LocalTime unformattedTime = LocalTime.now();
        DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = formattedTime.format(unformattedTime);

        System.out.println("[" + time + "]" + " [Sillicat] " + message);
    }

    // Use when logging crashes.
    public static void logError(String message){
        LocalTime unformattedTime = LocalTime.now();
        DateTimeFormatter formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = formattedTime.format(unformattedTime);

        System.err.println("[" + time + "]" + " [Sillicat] " + message);
    }
}
