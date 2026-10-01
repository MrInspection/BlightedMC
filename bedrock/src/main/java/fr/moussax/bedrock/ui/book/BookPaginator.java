package fr.moussax.bedrock.ui.book;

import fr.moussax.bedrock.utils.ColorUtils;
import org.bukkit.ChatColor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for word-wrapping and paginating text across Minecraft book pages.
 *
 * <p>Splits content based on the 114-pixel line width and 14 lines per page constraints.</p>
 */
public final class BookPaginator {

    private BookPaginator() {
    }

    /**
     * Paginates a long text string across multiple book pages.
     *
     * <p>Paragraphs are preserved and words are wrapped based on 114-pixel font metrics.
     * Each page contains at most 14 lines.</p>
     *
     * @param text the raw text to paginate
     * @return list of configured book pages
     */
    public static List<BookPage> paginateText(@Nullable String text) {
        if (text == null || text.isBlank()) {
            return List.of(new BookPage());
        }

        List<String> wrappedLines = wrapText(text);
        return paginateLines(wrappedLines);
    }

    /**
     * Splits a list of pre-formatted lines into pages of at most 14 lines.
     *
     * @param lines the lines to paginate
     * @return list of configured book pages
     */
    public static List<BookPage> paginateLines(@NonNull List<String> lines) {
        if (lines.isEmpty()) {
            return List.of(new BookPage());
        }

        List<BookPage> pages = new ArrayList<>();
        BookPage currentPage = new BookPage();
        int lineCount = 0;

        for (String line : lines) {
            if (lineCount >= BookCanvas.MAXIMUM_LINES_PER_PAGE) {
                pages.add(currentPage);
                currentPage = new BookPage();
                lineCount = 0;
            }

            currentPage.line(line);
            lineCount++;
        }

        if (lineCount > 0) {
            pages.add(currentPage);
        }

        return pages;
    }

    /**
     * Word-wraps text into individual lines that fit within the 114-pixel canvas width.
     *
     * @param text text to wrap
     * @return list of lines fitting the book width
     */
    public static List<String> wrapText(@NonNull String text) {
        List<String> wrappedLines = new ArrayList<>();
        String[] paragraphs = ColorUtils.colorize(text).split("\r?\n", -1);

        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                wrappedLines.add("");
                continue;
            }

            int leadingSpaces = 0;
            while (leadingSpaces < paragraph.length() && paragraph.charAt(leadingSpaces) == ' ') {
                leadingSpaces++;
            }
            String initialIndent = leadingSpaces > 0 ? " ".repeat(leadingSpaces) : "";
            boolean isFirstWord = true;

            String[] words = paragraph.split(" ");
            StringBuilder currentLine = new StringBuilder();
            String activeColors = "";

            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }

                if (currentLine.isEmpty()) {
                    String prefix = (isFirstWord ? initialIndent : "") + (activeColors.isEmpty() ? "" : activeColors);
                    isFirstWord = false;
                    if (BookCanvas.measurePixelWidth(prefix + word) > BookCanvas.PAGE_WIDTH_PIXELS) {
                        splitLargeWord(prefix + word, wrappedLines);
                        activeColors = ChatColor.getLastColors(wrappedLines.getLast());
                    } else {
                        currentLine.append(prefix).append(word);
                    }
                    continue;
                }

                String candidate = currentLine + " " + word;
                if (BookCanvas.measurePixelWidth(candidate) <= BookCanvas.PAGE_WIDTH_PIXELS) {
                    currentLine.append(" ").append(word);
                } else {
                    String finishedLine = currentLine.toString();
                    wrappedLines.add(finishedLine);
                    activeColors = ChatColor.getLastColors(finishedLine);
                    currentLine.setLength(0);

                    String prefix = activeColors.isEmpty() ? "" : activeColors;
                    if (BookCanvas.measurePixelWidth(prefix + word) > BookCanvas.PAGE_WIDTH_PIXELS) {
                        splitLargeWord(prefix + word, wrappedLines);
                        activeColors = ChatColor.getLastColors(wrappedLines.getLast());
                    } else {
                        currentLine.append(prefix).append(word);
                    }
                }
            }

            if (!currentLine.isEmpty()) {
                wrappedLines.add(currentLine.toString());
            }
        }

        return wrappedLines;
    }

    private static void splitLargeWord(String word, List<String> targetLines) {
        StringBuilder chunk = new StringBuilder();
        String activeColors;
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            String testChunk = chunk.toString() + character;
            if (BookCanvas.measurePixelWidth(testChunk) > BookCanvas.PAGE_WIDTH_PIXELS && !chunk.isEmpty()) {
                String finished = chunk.toString();
                targetLines.add(finished);
                activeColors = ChatColor.getLastColors(finished);
                chunk.setLength(0);
                if (!activeColors.isEmpty()) {
                    chunk.append(activeColors);
                }
            }
            chunk.append(character);
        }
        if (!chunk.isEmpty()) {
            targetLines.add(chunk.toString());
        }
    }
}
