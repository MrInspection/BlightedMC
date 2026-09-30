package fr.moussax.bedrock.ui.book;

import fr.moussax.bedrock.utils.ColorUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Fluent builder for composing individual pages of a {@link BookMenu}.
 *
 * <p>Designed specifically for the 114-pixel Minecraft written book canvas,
 * providing conveniences for centered headers, dividers, key-value fields,
 * buttons, and native client-side page navigation.</p>
 */
public final class BookPage {

    private final List<List<BaseComponent>> lines = new ArrayList<>();
    private final List<BaseComponent> currentLine = new ArrayList<>();
    private String id;

    /**
     * Constructs a new empty book page builder.
     */
    public BookPage() {
    }

    /**
     * Creates a new empty book page.
     *
     * @return a new book page builder
     */
    public static BookPage create() {
        return new BookPage();
    }

    /**
     * Sets the unique identifier or anchor name for this book page.
     *
     * @param id page identifier
     * @return this page builder
     */
    public BookPage id(@NonNull String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the configured identifier of this page, or {@code null} if unnamed.
     *
     * @return page identifier
     */
    public String id() {
        return id;
    }

    /**
     * Formats and centers a title with bold styling across the book page canvas.
     *
     * @param title the title text
     * @return this page builder
     */
    public BookPage title(@NonNull String title) {
        String formatted = "§0§l" + title;
        return line(BookCanvas.center(formatted));
    }

    /**
     * Centers text across the 114-pixel canvas and terminates the line.
     *
     * @param text the text to center
     * @return this page builder
     */
    public BookPage centered(@NonNull String text) {
        return line(BookCanvas.center(text));
    }

    /**
     * Appends a line of text followed by a newline break.
     *
     * @param text the text to append
     * @return this page builder
     */
    public BookPage line(@NonNull String text) {
        append(text);
        newLine();
        return this;
    }

    /**
     * Appends multiple lines of text, each followed by a newline break.
     *
     * @param lines lines to append
     * @return this page builder
     */
    public BookPage lines(@NonNull String... lines) {
        for (String line : lines) {
            line(line);
        }
        return this;
    }

    /**
     * Appends a collection of text lines, each followed by a newline break.
     *
     * @param lines collection of lines to append
     * @return this page builder
     */
    public BookPage lines(@NonNull Collection<String> lines) {
        for (String line : lines) {
            line(line);
        }
        return this;
    }

    /**
     * Appends a blank spacing line.
     *
     * @return this page builder
     */
    public BookPage blank() {
        return newLine();
    }

    /**
     * Appends multiple blank spacing lines.
     *
     * @param count number of blank lines to insert
     * @return this page builder
     */
    public BookPage blank(int count) {
        for (int index = 0; index < count; index++) {
            newLine();
        }
        return this;
    }

    /**
     * Appends a key-value field row followed by a newline break.
     *
     * <p>If the label and value exceed the canvas width, the content automatically word-wraps onto
     * subsequent lines with color preservation, preventing off-screen clipping.</p>
     *
     * @param label field label
     * @param value field value
     * @return this page builder
     */
    public BookPage field(@NonNull String label, @NonNull String value) {
        String fullText = "§0" + label + ": §8" + value;
        if (BookCanvas.measurePixelWidth(fullText) <= BookCanvas.PAGE_WIDTH_PIXELS) {
            return line(fullText);
        }
        return paragraph(fullText);
    }

    /**
     * Appends an italicized quote block followed by a newline break.
     *
     * <p>If the quote exceeds the canvas width, it automatically wraps onto subsequent lines.</p>
     *
     * @param quote text to quote
     * @return this page builder
     */
    public BookPage quote(@NonNull String quote) {
        String fullText = "§8\"§3" + quote + "§8\"";
        if (BookCanvas.measurePixelWidth(fullText) <= BookCanvas.PAGE_WIDTH_PIXELS) {
            return line(fullText);
        }
        return paragraph(fullText);
    }

    /**
     * Appends a paragraph of text, automatically word-wrapping lines to fit the canvas width.
     *
     * <p>Color formatting is preserved across line breaks.</p>
     *
     * @param paragraph text paragraph to wrap and append
     * @return this page builder
     */
    public BookPage paragraph(@NonNull String paragraph) {
        List<String> wrapped = BookPaginator.wrapText(paragraph);
        for (String line : wrapped) {
            line(line);
        }
        return this;
    }

    /**
     * Appends a collection of lore or description lines, wrapping any individual line that exceeds canvas width.
     *
     * @param lore lines of lore to append
     * @return this page builder
     */
    public BookPage lore(@NonNull List<String> lore) {
        for (String line : lore) {
            paragraph(line);
        }
        return this;
    }

    /**
     * Appends multiple lore or description lines, wrapping any individual line that exceeds canvas width.
     *
     * @param lore lines of lore to append
     * @return this page builder
     */
    public BookPage lore(@NonNull String... lore) {
        return lore(List.of(lore));
    }

    /**
     * Appends inline text without a trailing newline.
     *
     * @param text text to append
     * @return this page builder
     */
    public BookPage append(@NonNull String text) {
        return append(TextComponent.fromLegacy(ColorUtils.colorize(text)));
    }

    /**
     * Appends an existing chat component inline without a trailing newline.
     *
     * @param component component to append
     * @return this page builder
     */
    public BookPage append(@NonNull BaseComponent component) {
        currentLine.add(component);
        return this;
    }

    /**
     * Appends a clickable command execution button inline without a trailing newline.
     *
     * @param label     visible button label
     * @param hoverText hover tooltip text
     * @param command   command executed when clicked
     * @return this page builder
     */
    public BookPage appendButton(@NonNull String label, @NonNull String hoverText, @NonNull String command) {
        return appendInteractive(label, hoverText, ClickEvent.Action.RUN_COMMAND, command);
    }

    /**
     * Appends a clickable command execution button followed by a newline break.
     *
     * @param label     visible button label
     * @param hoverText hover tooltip text
     * @param command   command executed when clicked
     * @return this page builder
     */
    public BookPage button(@NonNull String label, @NonNull String hoverText, @NonNull String command) {
        appendButton(label, hoverText, command);
        newLine();
        return this;
    }

    /**
     * Appends a clickable command button centered horizontally on the canvas.
     *
     * @param label     visible button label
     * @param hoverText hover tooltip text
     * @param command   command executed when clicked
     * @return this page builder
     */
    public BookPage centeredButton(@NonNull String label, @NonNull String hoverText, @NonNull String command) {
        int spaces = BookCanvas.calculateCenterSpaces(label);
        if (spaces > 0) {
            append(" ".repeat(spaces));
        }
        return button(label, hoverText, command);
    }

    /**
     * Appends a clickable URL link inline without a trailing newline.
     *
     * @param label     visible link label
     * @param hoverText hover tooltip text
     * @param url       URL opened when clicked
     * @return this page builder
     */
    public BookPage appendLink(@NonNull String label, @NonNull String hoverText, @NonNull String url) {
        return appendInteractive(label, hoverText, ClickEvent.Action.OPEN_URL, url);
    }

    /**
     * Appends a clickable URL link followed by a newline break.
     *
     * @param label     visible link label
     * @param hoverText hover tooltip text
     * @param url       URL opened when clicked
     * @return this page builder
     */
    public BookPage link(@NonNull String label, @NonNull String hoverText, @NonNull String url) {
        appendLink(label, hoverText, url);
        newLine();
        return this;
    }

    /**
     * Appends a clickable URL link centered horizontally on the canvas.
     *
     * @param label     visible link label
     * @param hoverText hover tooltip text
     * @param url       URL opened when clicked
     * @return this page builder
     */
    public BookPage centeredLink(@NonNull String label, @NonNull String hoverText, @NonNull String url) {
        int spaces = BookCanvas.calculateCenterSpaces(label);
        if (spaces > 0) {
            append(" ".repeat(spaces));
        }
        return link(label, hoverText, url);
    }

    /**
     * Appends a native client-side page flip button inline without a trailing newline.
     *
     * @param label      visible button label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number or page identifier
     * @return this page builder
     */
    public BookPage appendPageButton(@NonNull String label, @NonNull String hoverText, @NonNull String targetPage) {
        return appendInteractive(label, hoverText, ClickEvent.Action.CHANGE_PAGE, targetPage);
    }

    /**
     * Appends a native client-side page flip button inline without a trailing newline.
     *
     * @param label      visible button label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number
     * @return this page builder
     */
    public BookPage appendPageButton(@NonNull String label, @NonNull String hoverText, int targetPage) {
        return appendPageButton(label, hoverText, String.valueOf(targetPage));
    }

    /**
     * Appends a native client-side page flip button followed by a newline break.
     *
     * @param label      visible button label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number or page identifier
     * @return this page builder
     */
    public BookPage pageButton(@NonNull String label, @NonNull String hoverText, @NonNull String targetPage) {
        appendPageButton(label, hoverText, targetPage);
        newLine();
        return this;
    }

    /**
     * Appends a native client-side page flip button followed by a newline break.
     *
     * @param label      visible button label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number
     * @return this page builder
     */
    public BookPage pageButton(@NonNull String label, @NonNull String hoverText, int targetPage) {
        return pageButton(label, hoverText, String.valueOf(targetPage));
    }

    /**
     * Appends a native client-side page navigation link followed by a newline break.
     *
     * @param label      visible link label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number or page identifier
     * @return this page builder
     */
    public BookPage pageLink(@NonNull String label, @NonNull String hoverText, @NonNull String targetPage) {
        return pageButton(label, hoverText, targetPage);
    }

    /**
     * Appends a native client-side page navigation link followed by a newline break.
     *
     * @param label      visible link label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number
     * @return this page builder
     */
    public BookPage pageLink(@NonNull String label, @NonNull String hoverText, int targetPage) {
        return pageButton(label, hoverText, targetPage);
    }

    /**
     * Appends a native client-side page flip button centered horizontally on the canvas.
     *
     * @param label      visible button label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number or page identifier
     * @return this page builder
     */
    public BookPage centeredPageButton(@NonNull String label, @NonNull String hoverText, @NonNull String targetPage) {
        int spaces = BookCanvas.calculateCenterSpaces(label);
        if (spaces > 0) {
            append(" ".repeat(spaces));
        }
        return pageButton(label, hoverText, targetPage);
    }

    /**
     * Appends a native client-side page flip button centered horizontally on the canvas.
     *
     * @param label      visible button label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number
     * @return this page builder
     */
    public BookPage centeredPageButton(@NonNull String label, @NonNull String hoverText, int targetPage) {
        return centeredPageButton(label, hoverText, String.valueOf(targetPage));
    }


    /**
     * Appends a clipboard copy button inline without a trailing newline.
     *
     * @param label     visible button label
     * @param hoverText hover tooltip text
     * @param text      text copied to clipboard when clicked
     * @return this page builder
     */
    public BookPage appendCopy(@NonNull String label, @NonNull String hoverText, @NonNull String text) {
        return appendInteractive(label, hoverText, ClickEvent.Action.COPY_TO_CLIPBOARD, text);
    }

    /**
     * Appends a clipboard copy button followed by a newline break.
     *
     * @param label     visible button label
     * @param hoverText hover tooltip text
     * @param text      text copied to clipboard when clicked
     * @return this page builder
     */
    public BookPage copy(@NonNull String label, @NonNull String hoverText, @NonNull String text) {
        appendCopy(label, hoverText, text);
        newLine();
        return this;
    }

    /**
     * Appends a clipboard copy button centered horizontally on the canvas.
     *
     * @param label     visible button label
     * @param hoverText hover tooltip text
     * @param text      text copied to clipboard when clicked
     * @return this page builder
     */
    public BookPage centeredCopy(@NonNull String label, @NonNull String hoverText, @NonNull String text) {
        int spaces = BookCanvas.calculateCenterSpaces(label);
        if (spaces > 0) {
            append(" ".repeat(spaces));
        }
        return copy(label, hoverText, text);
    }

    /**
     * Appends a primary-centered command action button followed by a newline.
     *
     * @param label     visible button label
     * @param hoverText hover tooltip text
     * @param command   command executed when clicked
     * @return this page builder
     */
    public BookPage actionButton(@NonNull String label, @NonNull String hoverText, @NonNull String command) {
        return centeredButton(label, hoverText, command);
    }

    /**
     * Appends a primary-centered web link action button followed by a newline.
     *
     * @param label     visible link label
     * @param hoverText hover tooltip text
     * @param url       web URL opened when clicked
     * @return this page builder
     */
    public BookPage actionLink(@NonNull String label, @NonNull String hoverText, @NonNull String url) {
        return centeredLink(label, hoverText, url);
    }

    /**
     * Appends a primary-centered clipboard copy action button followed by a newline.
     *
     * @param label     visible button label
     * @param hoverText hover tooltip text
     * @param text      text copied to clipboard when clicked
     * @return this page builder
     */
    public BookPage actionCopy(@NonNull String label, @NonNull String hoverText, @NonNull String text) {
        return centeredCopy(label, hoverText, text);
    }

    /**
     * Appends a primary-centered page navigation action button followed by a newline.
     *
     * @param label      visible button label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number or page identifier
     * @return this page builder
     */
    public BookPage actionPage(@NonNull String label, @NonNull String hoverText, @NonNull String targetPage) {
        return centeredPageButton(label, hoverText, targetPage);
    }

    /**
     * Appends a primary-centered page navigation action button followed by a newline.
     *
     * @param label      visible button label
     * @param hoverText  hover tooltip text
     * @param targetPage 1-based target page number
     * @return this page builder
     */
    public BookPage actionPage(@NonNull String label, @NonNull String hoverText, int targetPage) {
        return centeredPageButton(label, hoverText, targetPage);
    }

    /**
     * Checks if the given text or paragraph can fit within the page's remaining lines.
     *
     * @param text text to evaluate
     * @return true if the wrapped lines do not exceed remaining page capacity
     */
    public boolean fits(@NonNull String text) {
        return BookPaginator.wrapText(text).size() <= remainingLines();
    }

    /**
     * Terminates the current line with a newline break.
     *
     * @return this page builder
     */
    public BookPage newLine() {
        lines.add(new ArrayList<>(currentLine));
        currentLine.clear();
        return this;
    }

    /**
     * Returns the total number of lines currently added across this page builder.
     *
     * @return current line count
     */
    public int lineCount() {
        return lines.size() + (currentLine.isEmpty() ? 0 : 1);
    }

    /**
     * Returns the remaining lines on the current 14-line book page.
     *
     * @return remaining lines available on current page
     */
    public int remainingLines() {
        int modulo = lineCount() % BookCanvas.MAXIMUM_LINES_PER_PAGE;
        return modulo == 0 && lineCount() > 0 ? 0 : BookCanvas.MAXIMUM_LINES_PER_PAGE - modulo;
    }

    /**
     * Checks if the current page has reached its 14-line maximum capacity.
     *
     * @return true if line capacity is reached on the current page
     */
    public boolean isFull() {
        return remainingLines() == 0;
    }

    /**
     * Returns the total number of book pages generated by this page builder.
     *
     * @return page count (at least 1)
     */
    public int pageCount() {
        int total = lineCount();
        if (total <= 0) {
            return 1;
        }
        return (total + BookCanvas.MAXIMUM_LINES_PER_PAGE - 1) / BookCanvas.MAXIMUM_LINES_PER_PAGE;
    }

    private BookPage appendInteractive(String label, String hoverText, ClickEvent.Action action, String actionValue) {
        if (action == ClickEvent.Action.RUN_COMMAND && !actionValue.startsWith("/")) {
            actionValue = "/" + actionValue;
        }

        BaseComponent component = TextComponent.fromLegacy(ColorUtils.colorize(label));
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ColorUtils.colorize(hoverText)));
        ClickEvent clickEvent = new ClickEvent(action, actionValue);

        applyEvents(component, hoverEvent, clickEvent);
        return append(component);
    }

    private void applyEvents(BaseComponent component, HoverEvent hoverEvent, ClickEvent clickEvent) {
        component.setHoverEvent(hoverEvent);
        component.setClickEvent(clickEvent);
        if (component.getExtra() != null) {
            for (BaseComponent child : component.getExtra()) {
                applyEvents(child, hoverEvent, clickEvent);
            }
        }
    }

    /**
     * Builds and paginates this page's content into one or more pages of at most 14 lines each.
     *
     * <p>If content exceeds the 14-line limit of a single book page, lines automatically
     * spill onto consecutive pages, ensuring no trailing buttons or text are lost or clipped.</p>
     *
     * @return list of chat component arrays, one per resulting book page
     */
    public List<BaseComponent[]> buildPages() {
        List<List<BaseComponent>> allLines = getAllLines();
        if (allLines.isEmpty()) {
            return Collections.singletonList(new BaseComponent[0]);
        }

        List<BaseComponent[]> resultPages = new ArrayList<>();
        for (int i = 0; i < allLines.size(); i += BookCanvas.MAXIMUM_LINES_PER_PAGE) {
            int end = Math.min(i + BookCanvas.MAXIMUM_LINES_PER_PAGE, allLines.size());
            List<List<BaseComponent>> pageLines = allLines.subList(i, end);

            List<BaseComponent> pageComponents = new ArrayList<>();
            for (int lineIndex = 0; lineIndex < pageLines.size(); lineIndex++) {
                pageComponents.addAll(pageLines.get(lineIndex));
                if (lineIndex < pageLines.size() - 1) {
                    pageComponents.add(new TextComponent("\n"));
                }
            }
            resultPages.add(pageComponents.toArray(new BaseComponent[0]));
        }
        return resultPages;
    }

    /**
     * Builds the page content as an array of Bungee chat components.
     *
     * <p>If this page spans multiple pages, all lines are concatenated with newlines.</p>
     *
     * @return array of chat components ready for book metadata
     */
    public BaseComponent[] build() {
        List<List<BaseComponent>> allLines = getAllLines();
        List<BaseComponent> all = new ArrayList<>();
        for (int i = 0; i < allLines.size(); i++) {
            all.addAll(allLines.get(i));
            if (i < allLines.size() - 1) {
                all.add(new TextComponent("\n"));
            }
        }
        return all.toArray(new BaseComponent[0]);
    }

    private List<List<BaseComponent>> getAllLines() {
        List<List<BaseComponent>> result = new ArrayList<>(lines);
        if (!currentLine.isEmpty()) {
            result.add(new ArrayList<>(currentLine));
        }
        return result;
    }
}
