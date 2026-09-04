package fr.moussax.blightedMod.moderator.punishments;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser {
    private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+)([smhdwy])");

    public static Long parseDurationSeconds(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        Matcher matcher = DURATION_PATTERN.matcher(input.toLowerCase());
        if (!matcher.matches()) {
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException _) {
                return null;
            }
        }

        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "s" -> value;
            case "m" -> value * 60L;
            case "h" -> value * 3600L;
            case "d" -> value * 86400L;
            case "w" -> value * 604800L;
            case "y" -> value * 31536000L;
            default -> 0L;
        };
    }

    public static Long parseDuration(String input) {
        Long durationSeconds = parseDurationSeconds(input);
        if (durationSeconds == null) {
            return null;
        }
        return System.currentTimeMillis() + (durationSeconds * 1000L);
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

        int parsedAmount = Integer.parseInt(value);
        if (parsedAmount > 1) {
            unitName += "s";
        }
        return value + " " + unitName;
    }
}