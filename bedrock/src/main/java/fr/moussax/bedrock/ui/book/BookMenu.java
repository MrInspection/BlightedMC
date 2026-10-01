package fr.moussax.bedrock.ui.book;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Fluent builder for creating and opening interactive written books.
 *
 * <p>Supports custom titles, authors, book generations, structured page composition
 * via {@link BookPage}, and automatic multipage text pagination.</p>
 */
public final class BookMenu {

    private String title = "BlightedMenu";
    private String author = "BlightedMC";
    private BookMeta.Generation generation = BookMeta.Generation.ORIGINAL;
    private final List<BookPage> pages = new ArrayList<>();

    private BookMenu() {
    }

    /**
     * Creates a new book menu builder.
     *
     * @return a new book menu builder
     */
    public static BookMenu builder() {
        return new BookMenu();
    }

    /**
     * Sets the title of the book.
     *
     * @param title book title
     * @return this builder
     */
    public BookMenu title(@NonNull String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the author of the book.
     *
     * @param author book author
     * @return this builder
     */
    public BookMenu author(@NonNull String author) {
        this.author = author;
        return this;
    }

    /**
     * Sets the generation copy status of the book.
     *
     * @param generation book generation
     * @return this builder
     */
    public BookMenu generation(BookMeta.@NonNull Generation generation) {
        this.generation = generation;
        return this;
    }

    /**
     * Configures and appends a page using a callback.
     *
     * @param configurator callback to configure the page
     * @return this builder
     */
    public BookMenu page(@NonNull Consumer<BookPage> configurator) {
        BookPage page = new BookPage();
        configurator.accept(page);
        this.pages.add(page);
        return this;
    }

    /**
     * Configures and appends a named book page using a callback.
     *
     * @param id           unique identifier or anchor name for this page
     * @param configurator callback to configure the page
     * @return this builder
     */
    public BookMenu page(@NonNull String id, @NonNull Consumer<BookPage> configurator) {
        BookPage page = new BookPage().id(id);
        configurator.accept(page);
        this.pages.add(page);
        return this;
    }

    /**
     * Appends a pre-configured book page.
     *
     * @param page book page to append
     * @return this builder
     */
    public BookMenu page(@NonNull BookPage page) {
        this.pages.add(page);
        return this;
    }

    /**
     * Appends a pre-configured named book page.
     *
     * @param id   unique identifier or anchor name for this page
     * @param page book page to append
     * @return this builder
     */
    public BookMenu page(@NonNull String id, @NonNull BookPage page) {
        page.id(id);
        this.pages.add(page);
        return this;
    }

    /**
     * Appends multiple pre-configured book pages.
     *
     * @param pages book pages to append
     * @return this builder
     */
    public BookMenu pages(@NonNull BookPage... pages) {
        this.pages.addAll(List.of(pages));
        return this;
    }

    /**
     * Appends a collection of book pages.
     *
     * @param pages collection of book pages
     * @return this builder
     */
    public BookMenu pages(@NonNull Collection<BookPage> pages) {
        this.pages.addAll(pages);
        return this;
    }

    /**
     * Automatically word-wraps and paginates long text across multiple pages.
     *
     * @param text long text to paginate
     * @return this builder
     */
    public BookMenu paginatedText(@NonNull String text) {
        this.pages.addAll(BookPaginator.paginateText(text));
        return this;
    }

    /**
     * Paginates a list of pre-formatted lines across multiple pages.
     *
     * @param lines lines to paginate
     * @return this builder
     */
    public BookMenu paginatedLines(@NonNull List<String> lines) {
        this.pages.addAll(BookPaginator.paginateLines(lines));
        return this;
    }

    /**
     * Returns the configured title of the book.
     *
     * @return book title
     */
    public String title() {
        return title;
    }

    /**
     * Returns the configured author of the book.
     *
     * @return book author
     */
    public String author() {
        return author;
    }

    /**
     * Returns the configured generation copy tier of the book.
     *
     * @return book generation
     */
    public BookMeta.Generation generation() {
        return generation;
    }

    /**
     * Returns an unmodifiable snapshot of the configured book pages.
     *
     * @return list of book pages
     */
    public List<BookPage> pages() {
        return List.copyOf(pages);
    }

    /**
     * Builds and resolves all pages into an ordered list of physical book pages.
     *
     * <p>Any {@link net.md_5.bungee.api.chat.ClickEvent.Action#CHANGE_PAGE} click actions referencing
     * page names or logical builder page indices are resolved to their target physical page numbers,
     * accounting for any automatic multi-page overflow spilling across previous sections.</p>
     *
     * @return list of physical book pages ready for rendering
     */
    public List<BaseComponent[]> buildPhysicalPages() {
        if (pages.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> targetToPhysical = new HashMap<>();
        Map<Integer, Integer> logicalToPhysical = new HashMap<>();
        List<BaseComponent[]> physicalPages = new ArrayList<>();

        for (int index = 0; index < pages.size(); index++) {
            BookPage page = pages.get(index);
            int startPhysicalPage = physicalPages.size() + 1;
            logicalToPhysical.put(index + 1, startPhysicalPage);

            if (page.id() != null && !page.id().isBlank()) {
                targetToPhysical.put(page.id().trim().toLowerCase(Locale.ROOT), startPhysicalPage);
            }

            physicalPages.addAll(page.buildPages());
        }

        int totalPhysicalPages = physicalPages.size();
        List<BaseComponent[]> resolvedPages = new ArrayList<>(physicalPages.size());
        for (BaseComponent[] pageComponents : physicalPages) {
            BaseComponent[] resolvedComponents = new BaseComponent[pageComponents.length];
            for (int index = 0; index < pageComponents.length; index++) {
                BaseComponent component = pageComponents[index].duplicate();
                resolvePageLinks(component, targetToPhysical, logicalToPhysical, totalPhysicalPages);
                resolvedComponents[index] = component;
            }
            resolvedPages.add(resolvedComponents);
        }

        return resolvedPages;
    }

    private void resolvePageLinks(
            BaseComponent component,
            Map<String, Integer> targetToPhysical,
            Map<Integer, Integer> logicalToPhysical,
            int totalPhysicalPages
    ) {
        ClickEvent clickEvent = component.getClickEvent();
        if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String resolved = resolveTarget(clickEvent.getValue(), targetToPhysical, logicalToPhysical, totalPhysicalPages);
            if (!resolved.equals(clickEvent.getValue())) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, resolved));
            }
        }

        if (component.getExtra() != null) {
            for (BaseComponent extra : component.getExtra()) {
                resolvePageLinks(extra, targetToPhysical, logicalToPhysical, totalPhysicalPages);
            }
        }
    }

    private String resolveTarget(
            String target,
            Map<String, Integer> targetToPhysical,
            Map<Integer, Integer> logicalToPhysical,
            int totalPhysicalPages
    ) {
        if (target == null || target.isBlank()) {
            return "1";
        }

        Integer namedTarget = targetToPhysical.get(target.trim().toLowerCase(Locale.ROOT));
        if (namedTarget != null) {
            return String.valueOf(namedTarget);
        }

        try {
            int pageNumber = Integer.parseInt(target.trim());
            Integer logicalPhysical = logicalToPhysical.get(pageNumber);
            if (logicalPhysical != null) {
                return String.valueOf(logicalPhysical);
            }
            if (pageNumber >= 1 && pageNumber <= totalPhysicalPages) {
                return String.valueOf(pageNumber);
            }
        } catch (NumberFormatException ignored) {
        }

        return target;
    }

    /**
     * Assembles the book into an {@link ItemStack}.
     *
     * @return the configured written book item
     */
    public ItemStack toItemStack() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) {
            return book;
        }

        meta.setTitle(title);
        meta.setAuthor(author);
        meta.setGeneration(generation);

        List<BaseComponent[]> physicalPages = buildPhysicalPages();
        if (physicalPages.isEmpty()) {
            meta.spigot().addPage(new BaseComponent[0]);
        } else {
            for (BaseComponent[] pageComponents : physicalPages) {
                meta.spigot().addPage(pageComponents);
            }
        }

        book.setItemMeta(meta);
        return book;
    }

    /**
     * Builds and opens the book for the specified player.
     *
     * @param player player to receive and view the book
     */
    public void open(@NonNull Player player) {
        ItemStack book = toItemStack();
        player.openBook(book);
    }

    /**
     * Opens a single-page book for the player using default metadata.
     *
     * @param player       player viewing the book
     * @param configurator callback configuring the single page
     */
    public static void open(@NonNull Player player, @NonNull Consumer<BookPage> configurator) {
        builder().page(configurator).open(player);
    }

    /**
     * Opens a single-page book for the player with a custom title.
     *
     * @param player       player viewing the book
     * @param title        title of the book
     * @param configurator callback configuring the single page
     */
    public static void open(@NonNull Player player, @NonNull String title, @NonNull Consumer<BookPage> configurator) {
        builder().title(title).page(configurator).open(player);
    }
}
