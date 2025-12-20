package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract base class for menus that support automatic pagination for large item collections.
 *
 * <p>Displays a subset of items per page, with navigation buttons (Previous/Next) and a Close button.
 * Extend this class and implement {@link #getTotalItems(Player)} and {@link #getItem(Player, int)}
 * to provide the item collection.</p>
 */
public abstract class PaginatedMenu extends Menu {
    protected int currentPage = 0;
    protected int totalItems = 0;

    /**
     * Creates a new paginated menu.
     *
     * @param title menu title
     * @param size  inventory size (must be multiple of 9)
     */
    public PaginatedMenu(String title, int size) {
        super(title, size);
    }

    /**
     * Returns the total number of items to paginate for the given player.
     *
     * @param player player viewing the menu
     * @return total number of items
     */
    protected abstract int getTotalItems(Player player);

    /**
     * Returns the {@link ItemStack} representing the item at the specified global index.
     *
     * @param player player viewing the menu
     * @param index  global item index (not page-relative)
     * @return item to display at this index
     */
    protected abstract ItemStack getItem(Player player, int index);

    /**
     * Returns the number of items displayed per page.
     *
     * <p>By default, all slots except the bottom row are used for items.</p>
     *
     * @return number of items per page
     */
    protected int getItemsPerPage() {
        return size - 9;
    }

    /**
     * Builds the current page layout for the player.
     *
     * <ul>
     *   <li>Populates visible items for the current page</li>
     *   <li>Adds Previous/Next navigation buttons</li>
     *   <li>Adds a Close button and fills empty slots</li>
     * </ul>
     *
     * @param player player viewing the menu
     */
    @Override
    public void build(Player player) {
        totalItems = getTotalItems(player);

        int start = currentPage * getItemsPerPage();
        int end = Math.min(start + getItemsPerPage(), totalItems);

        int slotIndex = 0;
        for (int i = start; i < end; i++) {
            final int itemIndex = i;
            setItem(slotIndex++, getItem(player, itemIndex), MenuItemInteraction.ANY_CLICK, (p, t) -> onItemClick(p, itemIndex, t));
        }

        // Navigation buttons
        if (currentPage > 0) {
            setItem(size - 9, MenuElementPreset.BACK_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                currentPage--;
                MenuManager.openMenu(this, p);
            });
        }

        if (end < totalItems) {
            setItem(size - 1, MenuElementPreset.NEXT_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> {
                currentPage++;
                MenuManager.openMenu(this, p);
            });
        }

        setItem(size - 5, MenuElementPreset.CLOSE_BUTTON, MenuItemInteraction.ANY_CLICK, (p, t) -> close());
    }

    /**
     * Called when a player clicks an item in the paginated list.
     *
     * <p>Subclasses should override this to define specific behavior for item clicks.</p>
     *
     * @param player    player who clicked the item
     * @param index     global index of the clicked item
     * @param clickType type of click
     */
    protected void onItemClick(Player player, int index, ClickType clickType) {
        // Default: no action. Override in subclasses.
    }
}