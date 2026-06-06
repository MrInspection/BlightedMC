package fr.moussax.blightedMC.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for translating Minecraft color codes.
 */
public final class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private ColorUtils() {
    }

    /**
     * Translates legacy and hex color codes into Spigot formatting codes.
     *
     * @param text the text to colorize
     * @return the colorized text
     */
    public static String colorize(@NonNull String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 32);

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexCode).toString());
        }

        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    /**
     * Converts a hexadecimal RGB value into a Bukkit {@link Color}.
     *
     * @param hex the color in RRGGBB or #RRGGBB format
     * @return the corresponding color
     * @throws IllegalArgumentException if the color is invalid
     */
    public static Color fromHex(String hex) {
        if (!hex.matches("^#?[0-9a-fA-F]{6}$")) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
        return Color.fromRGB(Integer.parseInt(cleaned, 16));
    }

    public static List<String> colorize(@NonNull List<String> textList) {
        return textList.stream().map(ColorUtils::colorize).toList();
    }
}
