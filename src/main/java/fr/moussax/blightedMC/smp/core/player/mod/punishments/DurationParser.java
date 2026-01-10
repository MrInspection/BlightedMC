package fr.moussax.blightedMC.smp.core.player.mod.punishments;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([smhdwy])");

    public static Long parseDuration(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        Matcher matcher = DURATION_PATTERN.matcher(input.toLowerCase());
        if (!matcher.matches()) {
            return null;
        }

        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);

        long milliseconds = switch (unit) {
            case "s" -> TimeUnit.SECONDS.toMillis(value);
            case "m" -> TimeUnit.MINUTES.toMillis(value);
            case "h" -> TimeUnit.HOURS.toMillis(value);
            case "d" -> TimeUnit.DAYS.toMillis(value);
            case "w" -> TimeUnit.DAYS.toMillis(value * 7L);
            case "y" -> TimeUnit.DAYS.toMillis(value * 365L);
            default -> 0;
        };

        return System.currentTimeMillis() + milliseconds;
    }

    public static String formatDuration(String input) {
        Matcher matcher = DURATION_PATTERN.matcher(input.toLowerCase());
        if (!matcher.matches()) {
            return input;
        }

        String value = matcher.group(1);
        String unit = matcher.group(2);

        String unitName = switch (unit) {
            case "s" -> "second";
            case "m" -> "minute";
            case "h" -> "hour";
            case "d" -> "day";
            case "w" -> "week";
            case "y" -> "year";
            default -> "";
        };

        int val = Integer.parseInt(value);
        if (val > 1) {
            unitName += "s";
        }
        return value + " " + unitName;
    }
}
