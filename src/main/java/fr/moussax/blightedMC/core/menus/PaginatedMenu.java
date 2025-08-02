package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;

public abstract class PaginatedMenu extends Menu {
  protected int currentPage = 0;
  protected int totalItems = 0;

  public PaginatedMenu(String title, int size) {
    super(title, size);
  }

  protected abstract int getItemsCount();
  protected abstract ClickableItem getItemForPagination(int index);

  protected int getPageSize() {
    return size; // inventory size from Menu
  }

  protected int getPageCount() {
    return (int) Math.ceil((double) getItemsCount() / getPageSize());
  }

  @Override
  public void initializeItems(Player player) {
    totalItems = getItemsCount();
    int pageSize = getPageSize();
    int startIndex = currentPage * pageSize;
    int endIndex = Math.min(startIndex + pageSize, totalItems);

    clickableItems.clear();

    // Populate items for current page
    for (int slot = 0; slot < pageSize; slot++) {
      int itemIndex = startIndex + slot;
      if (itemIndex < endIndex) {
        ClickableItem item = getItemForPagination(itemIndex);
        if (item != null) setItemAt(slot, item);
      } else {
        setItemAt(slot, EMPTY_PANE());
      }
    }

    if (currentPage > 0) setItemAt(size - 9, BACK_BUTTON());
    if (currentPage < getPageCount() - 1) setItemAt(size - 1, NEXT_PAGE_BUTTON(this::nextPage));
    setItemAt(size - 5, CLOSE_BUTTON());
  }

  @Override
  public void open(Player player) {
    if (currentPage < 0) currentPage = 0;
    if (currentPage >= getPageCount()) currentPage = getPageCount() - 1;

    super.open(player);
  }

  public void nextPage(Player player) {
    if (currentPage + 1 >= getPageCount()) return;
    currentPage++;
    open(player);
  }

  public void previousPage(Player player) {
    if (currentPage - 1 < 0) return;
    currentPage--;
    open(player);
  }
}
