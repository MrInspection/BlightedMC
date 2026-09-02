package fr.moussax.bedrock.ui.book;

import fr.moussax.bedrock.text.InteractiveMessage;
import net.md_5.bungee.api.chat.BaseComponent;
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

    private final List<BaseComponent[]> pages = new ArrayList<>();

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
        this.pages.add(message.build());
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
        this.pages.add(message.build());
        return this;
    }

    /**
     * Opens the configured book for a player.
     *
     * <p>The generated book uses the default BlightedMC title and author.</p>
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

        for (BaseComponent[] pageComponents : pages) {
            meta.spigot().addPage(pageComponents);
        }

        book.setItemMeta(meta);
        player.openBook(book);
    }
}
