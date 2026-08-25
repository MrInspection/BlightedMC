package fr.moussax.blightedMC.shared.ui.menu.types;

import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.shared.ui.menu.interaction.MenuItemInteraction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * Base class for menus with automatic pagination.
 *
 * <p>Items are displayed across pages, with navigation controls rendered in
 * the bottom row. The current page is preserved when the menu is refreshed.</p>
 *
 * <p>Subclasses provide the total item count, item contents, and optional
 * click handling through {@link #onItemClick(Player, int, ClickType)}.</p>
 */
public abstract class PaginatedMenu extends Menu {

    protected int currentPage = 0;
    protected int totalItems = 0;

    /**
     * Creates a paginated menu.
     *
     * @param title menu title
     * @param size  inventory size (multiple of 9)
     */
    protected PaginatedMenu(String title, int size) {
        super(title, size);
    }

    /**
     * Returns the total number of items available for pagination.
     *
     * @param player player viewing the menu
     * @return total number of items
     */
    protected abstract int getTotalItems(@NonNull Player player);

    /**
     * Returns the item displayed at the specified global index.
     *
     * @param player player viewing the menu
     * @param index  global item index
     * @return item to display
     */
    protected abstract ItemStack getItem(@NonNull Player player, int index);

    /**
     * Returns the maximum number of items displayed on each page.
     *
     * <p>The bottom row is reserved for pagination controls.</p>
     *
     * @return number of items displayed per page
     */
    protected int getItemsPerPage() {
        return size - 9;
    }

    /**
     * Builds the current page and its navigation controls.
     *
     * <p>The current page is clamped to the last available page when the
     * total item count changes between refreshes.</p>
     *
     * @param player player viewing the menu
     */
    @Override
    public void build(@NonNull Player viewer) {
        totalItems = Math.max(0, getTotalItems(viewer));

        int itemsPerPage = getItemsPerPage();
        int maxPage = Math.max(0, (totalItems - 1) / itemsPerPage);
        currentPage = Math.min(currentPage, maxPage);

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            final int index = i;
            setItem(
                    slot++,
                    getItem(viewer, index),
                    MenuItemInteraction.ANY_CLICK,
                    (player, click) -> onItemClick(player, index, click)
            );
        }

        // Previous page
        if (currentPage > 0) {
            setBackButton(size - 9,
                    (player, type) -> {
                        currentPage--;
                        refresh(player);
                    }
            );
        }

        // Next page
        if (endIndex < totalItems) {
            setItem(size - 1, MenuElementPreset.NEXT_BUTTON,
                    (player, type) -> {
                        currentPage++;
                        refresh(player);
                    }
            );
        }

        setCloseButton(size - 5);
    }

    /**
     * Called when a paginated item is clicked.
     *
     * @param player    clicking player
     * @param index     global item index
     * @param clickType click type
     */
    protected void onItemClick(@NonNull Player player, int index, @NonNull ClickType clickType) {
        // override as needed
    }
}
