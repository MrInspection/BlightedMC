package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


/**
 * A menu that supports automatic pagination for large item collections.
 *
 * <p>Only a subset of items is displayed per page, with navigation buttons
 * for previous/next pages and a close button.</p>
 *
 * <p>Extend this class to implement a paginated menu by defining
 * {@link #getTotalItems(Player)} and {@link #getItem(Player, int)}.</p>
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
  public PaginatedMenu(String title, int size) {
    super(title, size);
  }

  /**
   * Returns the total number of items to paginate.
   *
   * @param player player viewing the menu
   * @return total item count
   */
  protected abstract int getTotalItems(Player player);

  /**
   * Returns the total number of items to paginate.
   *
   * @param player player viewing the menu
   * @return total item count
   */

  protected abstract ItemStack getItem(Player player, int index);

  /**
   * Returns the number of items displayed per page.
   *
   * <p>Default is {@code size - 9}, reserving the bottom row for navigation.</p>
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
    // Navigation
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
    fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
  }

  /**
   * Called when a player clicks an item in the paginated list.
   *
   * @param player    clicking player
   * @param index     global item index
   * @param clickType type of click
   */
  protected void onItemClick(Player player, int index, ClickType clickType) {
    // Override in subclasses to handle item clicks
  }
}