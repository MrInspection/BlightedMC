package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class PaginatedMenu extends Menu {
  protected int currentPage = 0;
  protected int totalItems = 0;

  public PaginatedMenu(String title, int size) {
    super(title, size);
  }

  protected abstract int getTotalItems(Player player);

  protected abstract ItemStack getItem(Player player, int index);

  protected int getItemsPerPage() {
    return size - 9;
  }

  @Override
  public void build(Player player) {
    totalItems = getTotalItems(player);
    int start = currentPage * getItemsPerPage();
    int end = Math.min(start + getItemsPerPage(), totalItems);
    int slotIndex = 0;
    for (int i = start; i < end; i++) {
      final int itemIndex = i;
      setItem(slotIndex++, getItem(player, itemIndex), ItemInteraction.ANY_CLICK, (p, t) -> onItemClick(p, itemIndex, t));
    }
    // Navigation
    if (currentPage > 0) {
      setItem(size - 9, MenuElementPreset.BACK_BUTTON, ItemInteraction.ANY_CLICK, (p, t) -> {
        currentPage--;
        MenuManager.openMenu(this, p);
      });
    }
    if (end < totalItems) {
      setItem(size - 1, MenuElementPreset.NEXT_BUTTON, ItemInteraction.ANY_CLICK, (p, t) -> {
        currentPage++;
        MenuManager.openMenu(this, p);
      });
    }
    setItem(size - 5, MenuElementPreset.CLOSE_BUTTON, ItemInteraction.ANY_CLICK, (p, t) -> close());
    fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
  }

  protected void onItemClick(Player player, int index, ClickType clickType) {
    // Override for item click handling
  }
}