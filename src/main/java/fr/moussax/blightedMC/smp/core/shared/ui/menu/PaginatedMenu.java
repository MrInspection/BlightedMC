package fr.moussax.blightedMC.smp.core.shared.ui.menu;

import fr.moussax.blightedMC.smp.core.shared.ui.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.interaction.MenuItemInteraction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

/**
 * Base class for menus supporting automatic pagination.
 *
 * <p>Pagination state is kept per-menu instance and refreshed in-place
 * to remain compatible with the MenuSystem lifecycle.</p>
 */
public abstract class PaginatedMenu extends Menu {

    protected int currentPage = 0;
    protected int totalItems = 0;

    protected PaginatedMenu(String title, int size) {
        super(title, size);
    }

    /**
     * @return total number of items to paginate
     */
    protected abstract int getTotalItems(@NonNull Player player);

    /**
     * @param index global item index
     * @return item for the given index
     */
    protected abstract ItemStack getItem(@NonNull Player player, int index);

    /**
     * @return number of items per page
     */
    protected int getItemsPerPage() {
        return size - 9;
    }

    @Override
    public void build(@NonNull Player player) {
        totalItems = Math.max(0, getTotalItems(player));

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
                getItem(player, index),
                MenuItemInteraction.ANY_CLICK,
                (p, click) -> onItemClick(p, index, click)
            );
        }

        // Previous page
        if (currentPage > 0) {
            setItem(
                size - 9,
                MenuElementPreset.BACK_BUTTON,
                MenuItemInteraction.ANY_CLICK,
                (p, t) -> {
                    currentPage--;
                    refresh(p);
                }
            );
        }

        // Next page
        if (endIndex < totalItems) {
            setItem(
                size - 1,
                MenuElementPreset.NEXT_BUTTON,
                MenuItemInteraction.ANY_CLICK,
                (p, t) -> {
                    currentPage++;
                    refresh(p);
                }
            );
        }

        // Close button
        setItem(
            size - 5,
            MenuElementPreset.CLOSE_BUTTON,
            MenuItemInteraction.ANY_CLICK,
            (p, t) -> close()
        );
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
