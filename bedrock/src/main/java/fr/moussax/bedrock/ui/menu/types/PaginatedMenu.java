package fr.moussax.bedrock.ui.menu.types;

import fr.moussax.bedrock.ui.menu.Menu;
import fr.moussax.bedrock.ui.menu.interaction.MenuElementPreset;
import fr.moussax.bedrock.ui.menu.interaction.MenuItemInteraction;
import lombok.Getter;
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
@Getter
public abstract class PaginatedMenu extends Menu {

    public static final int[] INNER_GRID_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

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
     * Returns the target inventory slot indices for displaying paginated items.
     *
     * <p>If {@code null}, items are placed sequentially starting at slot 0.</p>
     *
     * @return slot index array, or {@code null} for default sequential placement
     */
    protected int[] getDisplaySlots() {
        return null;
    }

    /**
     * Returns an item to display when the menu has no items, or {@code null}.
     *
     * @param player player viewing the menu
     * @return empty state item, or {@code null} if none
     */
    protected ItemStack getEmptyStateItem(@NonNull Player player) {
        return null;
    }

    /**
     * Returns the 1-based number of the current page.
     *
     * @return current page number (1-based)
     */
    public int getCurrentPageNumber() {
        return currentPage + 1;
    }

    /**
     * Returns the total number of pages based on total items and items per page.
     *
     * @return total number of pages
     */
    public int getTotalPages() {
        int itemsPerPage = getItemsPerPage();
        if (itemsPerPage <= 0) return 1;
        return Math.max(1, (totalItems + itemsPerPage - 1) / itemsPerPage);
    }

    /**
     * Returns the maximum number of items displayed on each page.
     *
     * <p>The bottom row is reserved for pagination controls.</p>
     *
     * @return number of items displayed per page
     */
    protected int getItemsPerPage() {
        int[] displaySlots = getDisplaySlots();
        if (displaySlots != null) {
            return displaySlots.length;
        }
        return size - 9;
    }

    /**
     * Builds the current page and its navigation controls.
     *
     * <p>The current page is clamped to the last available page when the
     * total item count changes between refreshes.</p>
     *
     * @param viewer player viewing the menu
     */
    @Override
    public void build(@NonNull Player viewer) {
        totalItems = Math.max(0, getTotalItems(viewer));
        clearInventory();

        int closeSlot = size - 5;
        int backSlot = size - 6;
        int nextSlot = size - 4;

        ItemStack emptyItem = getEmptyStateItem(viewer);
        if (totalItems == 0) {
            if (emptyItem != null) {
                int emptySlot = size >= 27 ? 22 : size / 2;
                setItem(emptySlot, emptyItem, MenuItemInteraction.ANY_CLICK, (_, _) -> { });
            }
            setCloseButton(closeSlot);
            return;
        }

        int itemsPerPage = getItemsPerPage();
        int maxPage = (totalItems - 1) / itemsPerPage;
        currentPage = Math.min(currentPage, maxPage);

        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        int[] displaySlots = getDisplaySlots();
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            final int index = i;
            int slot = displaySlots != null ? displaySlots[slotIndex++] : i - startIndex;
            setItem(
                    slot,
                    getItem(viewer, index),
                    MenuItemInteraction.ANY_CLICK,
                    (player, click) -> {
                        if (click.isShiftClick()) {
                            onItemShiftClick(player, index);
                        } else if (click.isRightClick()) {
                            onItemRightClick(player, index);
                        } else if (click.isLeftClick()) {
                            onItemLeftClick(player, index);
                        }
                        onItemClick(player, index, click);
                    }
            );
        }

        if (currentPage > 0) {
            setBackButton(backSlot, (player, _) -> {
                currentPage--;
                refresh(player);
            });
        }

        if (endIndex < totalItems) {
            setItem(nextSlot, MenuElementPreset.NEXT_BUTTON, (player, _) -> {
                currentPage++;
                refresh(player);
            });
        }

        setCloseButton(closeSlot);
    }

    /**
     * Called when a paginated item is clicked with a left-click.
     *
     * @param player clicking player
     * @param index  global item index
     */
    protected void onItemLeftClick(@NonNull Player player, int index) {
        // override as needed
    }

    /**
     * Called when a paginated item is clicked with a right-click.
     *
     * @param player clicking player
     * @param index  global item index
     */
    protected void onItemRightClick(@NonNull Player player, int index) {
        // override as needed
    }

    /**
     * Called when a paginated item is clicked with a shift-click.
     *
     * @param player clicking player
     * @param index  global item index
     */
    protected void onItemShiftClick(@NonNull Player player, int index) {
        // override as needed
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
