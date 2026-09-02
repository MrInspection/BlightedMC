package fr.moussax.bedrock.ui.sign;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Contains the text submitted through a sign input menu.
 *
 * @param player player who submitted the input
 * @param lines submitted sign lines
 */
public record SignInputResult(@NonNull Player player, String @NonNull [] lines) {

    /**
     * Returns the first submitted line.
     *
     * @return first line, or an empty string if unavailable
     */
    public String getFirstLine() {
        return getLine(0);
    }

    /**
     * Returns the submitted line at the specified index.
     *
     * @param index line index
     * @return line content, or an empty string if the index is invalid or the submitted line is null
     */
    public String getLine(int index) {
        if (index < 0 || index >= lines.length) return "";
        return lines[index] != null ? lines[index] : "";
    }
}
