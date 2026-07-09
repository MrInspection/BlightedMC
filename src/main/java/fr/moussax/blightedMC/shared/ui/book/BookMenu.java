package fr.moussax.blightedMC.shared.ui.book;

import fr.moussax.blightedMC.utils.Formatter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class BookMenu {

    private final List<BaseComponent[]> pages = new ArrayList<>();

    private BookMenu() {
    }

    /**
     * Creates a new empty book menu builder.
     *
     * @return a new book menu instance
     */
    public static BookMenu builder() {
        return new BookMenu();
    }

    /**
     * Appends a complete page constructed from an interactive message.
     *
     * @param message the configured interactive message
     * @return this builder
     */
    public BookMenu addPage(Formatter.@NonNull InteractiveMessage message) {
        this.pages.add(message.build());
        return this;
    }

    /**
     * Appends a page configured through a functional message builder.
     *
     * <p>The configurator receives an empty interactive message instance that can
     * be populated before the page is compiled.</p>
     *
     * @param pageConfigurator the page configuration callback
     * @return this builder
     */
    public BookMenu addPage(@NonNull Consumer<Formatter.InteractiveMessage> pageConfigurator) {
        Formatter.InteractiveMessage message = Formatter.text("");
        pageConfigurator.accept(message);
        this.pages.add(message.build());
        return this;
    }

    /**
     * Compiles the configured pages and opens the generated book for a player.
     *
     * <p>The generated book uses the server default title and author metadata.</p>
     *
     * @param player the player receiving the book
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
