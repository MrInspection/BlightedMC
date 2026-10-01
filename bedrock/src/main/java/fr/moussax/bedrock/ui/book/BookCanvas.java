package fr.moussax.bedrock.ui.book;

import fr.moussax.bedrock.utils.ColorUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Metric constants and layout calculations for Minecraft written book canvases.
 *
 * <p>A Minecraft written book page renders at an exact width of 114 pixels
 * with a maximum of 14 lines per page. Text layout calculations use standard
 * Minecraft font metrics.</p>
 */
public final class BookCanvas {

    /**
     * Maximum safe visible horizontal width of a book page in pixels.
     *
     * <p>Although the raw book canvas is 114 pixels wide, Minecraft appends a mandatory
     * 1-pixel spacer after every character; using 113 pixels prevents edge-case unwanted line breaks.</p>
     */
    public static final int PAGE_WIDTH_PIXELS = 113;

    /**
     * Maximum number of lines that fit on a single book page before truncation.
     */
    public static final int MAXIMUM_LINES_PER_PAGE = 14;

    /**
     * Standard pixel width of a space character including inter-character spacing.
     */
    public static final int SPACE_PIXEL_WIDTH = 4;

    private BookCanvas() {
    }

    /**
     * Calculates the exact pixel width of a string rendered in Minecraft's default font.
     *
     * <p>Color and format codes (§ and &) are accounted for; bold styling adds 1 pixel per character.</p>
     *
     * @param text the text to measure
     * @return total width in pixels
     */
    public static int measurePixelWidth(@Nullable String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String colorized = ColorUtils.colorize(text);
        int width = 0;
        boolean isBold = false;
        int length = colorized.length();

        for (int index = 0; index < length; index++) {
            char character = colorized.charAt(index);

            if (character == '§' && index + 1 < length) {
                char code = Character.toLowerCase(colorized.charAt(index + 1));
                if (code == 'l') {
                    isBold = true;
                } else if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f') || code == 'r') {
                    isBold = false;
                }
                index++;
                continue;
            }

            int charWidth = getCharacterWidth(character);
            if (isBold) {
                charWidth += 1;
            }
            width += charWidth + 1;
        }

        return width;
    }

    /**
     * Calculates the number of leading spaces needed to center text horizontally.
     *
     * @param text the text to center
     * @return count of leading spaces
     */
    public static int calculateCenterSpaces(@NonNull String text) {
        int textWidth = measurePixelWidth(text);
        if (textWidth >= PAGE_WIDTH_PIXELS) {
            return 0;
        }

        int remainingPixels = PAGE_WIDTH_PIXELS - textWidth;
        int paddingPixels = remainingPixels / 2;
        return Math.max(0, paddingPixels / SPACE_PIXEL_WIDTH);
    }

    /**
     * Centers text horizontally across the 114-pixel book page width by prepending space padding.
     *
     * @param text the text to center
     * @return centered text padded with leading spaces
     */
    public static String center(@NonNull String text) {
        int spaces = calculateCenterSpaces(text);
        return spaces > 0 ? " ".repeat(spaces) + text : text;
    }

    private static int getCharacterWidth(char character) {
        return switch (character) {
            case '!', ',', '.', ':', ';', 'i', '|' -> 1;
            case '\'', '`' -> 2;
            case ' ', 'l', 'I', '[', ']', 't' -> 3;
            case 'f', 'k', '"', '*', '(', ')', '{', '}', '<', '>' -> 4;
            case '@', '~', 'M', 'W' -> 6;
            default -> 5;
        };
    }
}
