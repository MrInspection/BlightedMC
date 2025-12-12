package fr.moussax.blightedMC.utils.formatting;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.repeat;

/**
 * Unified utility class for text formatting, number formatting, and player messaging.
 *
 * <p>Provides methods for:
 * <ul>
 *   <li>String formatting (capitalization, enum names, time)</li>
 *   <li>Number formatting (decimals, compact notation, Roman numerals)</li>
 *   <li>Player messaging (info, warnings, admin checks)</li>
 *   <li>Interactive chat components (clickable, hoverable text)</li>
 * </ul>
 */
public final class Formatter {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");
    private static final String ADMIN_PERMISSION = "blightedmc.admin";
    private static final String INFO_PREFIX = "§8 ■ §7";
    private static final String WARN_PREFIX = "§4 ■ §c";

    private Formatter() {
    }

    /**
     * Capitalizes the first letter of a string and lowercases the rest.
     *
     * @param text the string to capitalize
     * @return capitalized string
     */
    public static String capitalize(String text) {
        if (text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    /**
     * Converts UPPER_CASE_UNDERSCORE format to Title Case with spaces.
     *
     * @param input the enum-style string to format
     * @return formatted string (e.g., "RANGED_WEAPON" → "Ranged Weapon")
     */
    public static String formatEnumName(@Nullable String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String[] words = input.split("_");
        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            formatted.append(Character.toUpperCase(word.charAt(0)))
                .append(word.substring(1));
            if (i < words.length - 1) {
                formatted.append(' ');
            }
        }

        return formatted.toString();
    }

    /**
     * Formats time in seconds to "mm:ss" format.
     *
     * @param timeInSeconds time in seconds
     * @return formatted time string (e.g., 123 → "02:03")
     */

    public static String formatTime(long timeInSeconds) {
        long minutes = timeInSeconds / 60;
        long seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Formats a double to a string with specified decimal places.
     *
     * @param value         the value to format
     * @param decimalPlaces number of decimal places
     * @return formatted string (e.g., 1.97349873, 3 → "1.973")
     */

    public static String formatDouble(double value, int decimalPlaces) {
        if (decimalPlaces == 0 || value == Math.floor(value)) {
            return String.format("%.0f", value);
        }
        String format = "%." + decimalPlaces + "f";
        return String.format(format, value).replaceAll("\\.?0+$", "");
    }

    /**
     * Formats a double with comma separators.
     *
     * @param value the value to format
     * @return formatted string with commas (e.g., 1234.56 → "1,235")
     */

    public static String formatDecimalWithCommas(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    /**
     * Compacts large numbers into abbreviated format (K, M, B).
     *
     * @param value the number to compact
     * @return compact notation (e.g., 1500 → "1.5k", 2_000_000 → "2M")
     */

    public static String compactNumber(int value) {
        String[] units = {"", "k", "M", "B"};
        int unitIndex = 0;

        double compactValue = value;
        while (compactValue >= 1000 && unitIndex < units.length - 1) {
            compactValue /= 1000;
            unitIndex++;
        }

        BigDecimal decimal = new BigDecimal(compactValue);
        decimal = decimal.setScale(1, RoundingMode.HALF_EVEN);
        String formatted = decimal.stripTrailingZeros().toPlainString();

        return formatted + units[unitIndex];
    }

    /**
     * Converts an integer to Roman numeral representation.
     *
     * @param number the number to convert (1-3999)
     * @return Roman numeral string
     * @throws IllegalArgumentException if the number is out of the valid range
     */

    public static String toRomanNumeral(int number) {
        if (number <= 0 || number > 3999) {
            throw new IllegalArgumentException("Number out of range for Roman numerals: " + number);
        }

        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return thousands[number / 1000]
            + hundreds[(number % 1000) / 100]
            + tens[(number % 100) / 10]
            + ones[number % 10];
    }

    /**
     * Sends informational messages to a command sender with a gray prefix.
     *
     * @param sender   the recipient
     * @param messages list of messages to send
     */
    public static void inform(CommandSender sender, @NonNull List<String> messages) {
        messages.forEach(message -> sender.sendMessage(INFO_PREFIX + message));
    }

    /**
     * Sends informational messages to a command sender.
     *
     * @param sender   the recipient
     * @param messages messages to send (no prefix applied)
     */
    public static void inform(@NonNull CommandSender sender, @NonNull String... messages) {
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    /**
     * Sends a single informational message to a command sender.
     *
     * @param sender  the recipient
     * @param message the message to send
     */
    public static void inform(@NonNull CommandSender sender, @NonNull String message) {
        inform(sender, Collections.singletonList(message));
    }

    /**
     * Sends warning messages to a command sender with red prefix and error sound.
     *
     * @param sender   the recipient
     * @param messages list of warning messages
     */
    public static void warn(@NonNull CommandSender sender, @NonNull List<String> messages) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        }
        messages.forEach(message -> sender.sendMessage(WARN_PREFIX + message));
    }

    /**
     * Sends warning messages to a command sender with error sound.
     *
     * @param sender   the recipient
     * @param messages warning messages (no prefix applied)
     */
    public static void warn(@NonNull CommandSender sender, @NonNull String... messages) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        }
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    /**
     * Sends a single warning message to a command sender.
     *
     * @param sender  the recipient
     * @param message the warning message
     */
    public static void warn(@NonNull CommandSender sender, @NonNull String message) {
        warn(sender, Collections.singletonList(message));
    }

    /**
     * Verifies admin permission for a player.
     *
     * <p>Requires both operator status and the admin permission.
     * Sends a warning message if the check fails.
     *
     * @param player the player to check
     * @return {@code true} if player has admin permission
     */
    public static boolean enforceAdminPermission(@NonNull Player player) {
        if (!player.isOp() || !player.hasPermission(ADMIN_PERMISSION)) {
            warn(player, "You must be an §4ADMIN §cto use this command.");
            return false;
        }
        return true;
    }

    /**
     * Creates an interactive text component with hover and optional click action.
     *
     * @param text        the visible text
     * @param hoverText   text displayed on hover
     * @param clickAction the click action type (or {@code null})
     * @param clickValue  the value for the click action (or {@code null})
     * @return configured text component
     */
    @SuppressWarnings("deprecation")
    public static TextComponent createInteractiveText(String text, String hoverText, ClickEvent.Action clickAction, String clickValue) {
        TextComponent component = new TextComponent(text);
        component.setHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new ComponentBuilder(hoverText).create()
        ));

        if (clickAction != null && clickValue != null) {
            component.setClickEvent(new ClickEvent(clickAction, clickValue));
        }

        return component;
    }

    /**
     * Creates a progress bar string representation.
     *
     * @param percent the current progress value
     * @param max     the maximum number of segments in the bar
     * @param bar     the value each segment represents
     * @return formatted progress bar string
     */
    public static String createProgressBar(double percent, double max, double bar) {
        double filledBars = percent / bar;
        double emptyBars = max - filledBars;

        if (filledBars > max) filledBars = max;

        return ChatColor.DARK_GREEN + repeat("-", (int) filledBars) + ChatColor.WHITE + repeat("-", (int) emptyBars);
    }
}