package fr.moussax.bedrock.ui.book;

import fr.moussax.bedrock.text.InteractiveMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builds and opens interactive written books for players.
 *
 * <p>Pages are composed from {@link InteractiveMessage} instances
 * and may contain interactive chat components.</p>
 */
public final class BookMenu {

    private static final int MAXIMUM_CHARACTERS_PER_LINE = 19;
    private static final int MAXIMUM_LINES_PER_PAGE = 14;

    private final List<BaseComponent[]> rawPages = new ArrayList<>();

    private BookMenu() {
    }

    /**
     * Creates an empty book menu.
     *
     * @return new book menu
     */
    public static BookMenu builder() {
        return new BookMenu();
    }

    /**
     * Appends a page from an interactive message.
     *
     * @param message message used to build the page
     * @return this book menu
     */
    public BookMenu addPage(@NonNull InteractiveMessage message) {
        this.rawPages.add(message.build());
        return this;
    }

    /**
     * Appends a page configured through a callback.
     *
     * <p>The callback receives an empty interactive message that can be
     * configured before the page is built.</p>
     *
     * @param pageConfigurator callback used to configure the page
     * @return this book menu
     */
    public BookMenu addPage(@NonNull Consumer<InteractiveMessage> pageConfigurator) {
        InteractiveMessage message = InteractiveMessage.text("");
        pageConfigurator.accept(message);
        this.rawPages.add(message.build());
        return this;
    }

    /**
     * Opens the configured book for a player.
     *
     * <p>The generated book uses the default BlightedMC title and author,
     * and automatically paginates content overflowing single-page dimensions.</p>
     *
     * @param player player receiving the book
     */
    public void open(@NonNull Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return;

        meta.setTitle("BlightedMenu");
        meta.setAuthor("BlightedMC");
        meta.setGeneration(BookMeta.Generation.ORIGINAL);

        List<BaseComponent[]> paginatedPages = buildPaginatedPages();
        for (BaseComponent[] pageComponents : paginatedPages) {
            meta.spigot().addPage(pageComponents);
        }

        book.setItemMeta(meta);
        player.openBook(book);
    }

    private List<BaseComponent[]> buildPaginatedPages() {
        List<BaseComponent[]> formattedPages = new ArrayList<>();

        for (BaseComponent[] pageComponents : rawPages) {
            List<BaseComponent[]> splitPages = splitSinglePageComponents(pageComponents);
            formattedPages.addAll(splitPages);
        }

        return formattedPages;
    }

    private List<BaseComponent[]> splitSinglePageComponents(BaseComponent[] pageComponents) {
        List<TextComponent> flattenedComponents = new ArrayList<>();
        for (BaseComponent baseComponent : pageComponents) {
            flattenComponent(baseComponent, flattenedComponents);
        }

        List<List<BaseComponent>> lines = new ArrayList<>();
        List<BaseComponent> currentLine = new ArrayList<>();
        int currentLineLength = 0;

        for (TextComponent component : flattenedComponents) {
            String componentText = component.getText();
            if (componentText == null || componentText.isEmpty()) {
                continue;
            }

            String[] paragraphs = componentText.split("\r?\n", -1);
            for (int paragraphIndex = 0; paragraphIndex < paragraphs.length; paragraphIndex++) {
                if (paragraphIndex > 0) {
                    lines.add(currentLine);
                    currentLine = new ArrayList<>();
                    currentLineLength = 0;
                }

                String paragraphText = paragraphs[paragraphIndex];
                if (paragraphText.isEmpty()) {
                    continue;
                }

                String[] words = paragraphText.split(" ", -1);
                for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
                    String word = words[wordIndex];
                    if (wordIndex > 0 && !currentLine.isEmpty() && words.length > 1) {
                        word = " " + word;
                    }

                    while (word.length() > MAXIMUM_CHARACTERS_PER_LINE) {
                        String chunk = word.substring(0, MAXIMUM_CHARACTERS_PER_LINE);
                        word = word.substring(MAXIMUM_CHARACTERS_PER_LINE);

                        TextComponent chunkComponent = duplicateComponentWithText(component, chunk);
                        if (currentLineLength + chunk.length() > MAXIMUM_CHARACTERS_PER_LINE && !currentLine.isEmpty()) {
                            lines.add(currentLine);
                            currentLine = new ArrayList<>();
                            currentLineLength = 0;
                        }
                        currentLine.add(chunkComponent);
                        lines.add(currentLine);
                        currentLine = new ArrayList<>();
                        currentLineLength = 0;
                    }

                    if (word.isEmpty()) {
                        continue;
                    }

                    if (currentLineLength + word.length() > MAXIMUM_CHARACTERS_PER_LINE && !currentLine.isEmpty()) {
                        lines.add(currentLine);
                        currentLine = new ArrayList<>();
                        currentLineLength = 0;
                        if (word.startsWith(" ")) {
                            word = word.substring(1);
                        }
                    }

                    TextComponent wordComponent = duplicateComponentWithText(component, word);
                    currentLine.add(wordComponent);
                    currentLineLength += word.length();
                }
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }

        if (lines.isEmpty()) {
            return List.of(new BaseComponent[][]{pageComponents});
        }

        List<BaseComponent[]> resultPages = new ArrayList<>();
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex += MAXIMUM_LINES_PER_PAGE) {
            int endIndex = Math.min(lineIndex + MAXIMUM_LINES_PER_PAGE, lines.size());
            List<List<BaseComponent>> pageLines = lines.subList(lineIndex, endIndex);

            List<BaseComponent> pageComponentsList = new ArrayList<>();
            for (int lineSubIndex = 0; lineSubIndex < pageLines.size(); lineSubIndex++) {
                List<BaseComponent> line = pageLines.get(lineSubIndex);
                pageComponentsList.addAll(line);
                if (lineSubIndex < pageLines.size() - 1) {
                    pageComponentsList.add(new TextComponent("\n"));
                }
            }

            resultPages.add(pageComponentsList.toArray(new BaseComponent[0]));
        }

        return resultPages;
    }

    private void flattenComponent(BaseComponent component, List<TextComponent> targetList) {
        if (component instanceof TextComponent textComponent) {
            targetList.add(textComponent);
        }
        if (component.getExtra() != null) {
            for (BaseComponent extraComponent : component.getExtra()) {
                flattenComponent(extraComponent, targetList);
            }
        }
    }

    private TextComponent duplicateComponentWithText(TextComponent source, String text) {
        TextComponent copy = new TextComponent(text);
        copy.setColor(source.getColor());
        copy.setBold(source.isBoldRaw());
        copy.setItalic(source.isItalicRaw());
        copy.setUnderlined(source.isUnderlinedRaw());
        copy.setStrikethrough(source.isStrikethroughRaw());
        copy.setObfuscated(source.isObfuscatedRaw());
        copy.setHoverEvent(source.getHoverEvent());
        copy.setClickEvent(source.getClickEvent());
        return copy;
    }
}
