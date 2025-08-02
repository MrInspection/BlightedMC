package fr.moussax.blightedMC.core.menus;

import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Menu implements BlightedInventory {

  public static ClickableItem EMPTY_PANE() {
    ItemBuilder emptyPane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, " ");
    return new ClickableItem(emptyPane.toItemStack(), player -> {});
  }

  public static ClickableItem BACK_BUTTON() {
    ItemBuilder backButton = new ItemBuilder(Material.ARROW, 1, "§cBack");
    return new ClickableItem(backButton.toItemStack(), player -> MenuRouter.goBack(player));
  }

  public static ClickableItem NEXT_PAGE_BUTTON(Consumer<Player> onClick) {
    ItemBuilder nextButton = new ItemBuilder(Material.ARROW, 1, "§aNext Page");
    return new ClickableItem(nextButton.toItemStack(), onClick);
  }

  public static ClickableItem CLOSE_BUTTON() {
    ItemBuilder closeButton = new ItemBuilder(Material.BARRIER, 1, "§cClose");
    return new ClickableItem(closeButton.toItemStack(), player -> MenuRouter.closeCurrentMenu(player));
  }

  protected final Inventory inventory;
  protected final int size;
  protected final String title;
  protected final Map<Integer, ClickableItem> clickableItems = new HashMap<>();

  private int incrementalSlot = 0;

  public abstract void initializeItems(Player player);

  public Menu(String title, int size) {
    this.size = size;
    this.title = title;
    this.inventory = Bukkit.createInventory(this, size, title);
  }

  public void open(Player player) {
    this.clickableItems.clear();
    this.incrementalSlot = 0;

    this.initializeItems(player);
    player.openInventory(this.inventory);
  }

  public void close(Player player) {
    player.closeInventory();
  }

  public void setItemAt(int slot, ClickableItem item) {
    this.clickableItems.put(slot, item);
    this.inventory.setItem(slot, item.getItem());
  }

  public void setItemAt(int x, int y, ClickableItem item) {
    setItemAt((x - 1) + ((y - 1) * 9), item);
  }

  public void setNextItem(ClickableItem item) {
    while (incrementalSlot < this.inventory.getSize() &&
        this.clickableItems.containsKey(incrementalSlot)) {
      incrementalSlot++;
    }

    if (incrementalSlot >= this.inventory.getSize()) {
      throw new IndexOutOfBoundsException("The inventory is full!");
    }

    setItemAt(incrementalSlot, item);
    incrementalSlot++;
  }

  @Override
  public ClickableItem getItemAt(int slot) {
    return this.clickableItems.getOrDefault(slot, null);
  }

  /**
   * Fill every remaining slot with an empty pane.
   * This method updates both the clickable map and the actual inventory.
   */
  public void fillRemainingSlotsWithEmptyPanes() {
    for (int slot = 0; slot < this.inventory.getSize(); slot++) {
      if (!this.clickableItems.containsKey(slot)) {
        ClickableItem empty = EMPTY_PANE();
        this.clickableItems.put(slot, empty);
        this.inventory.setItem(slot, empty.getItem());
      }
    }
  }

  public enum BorderType { TOP, BOTTOM, LEFT, RIGHT, ALL }
  public enum Direction { HORIZONTAL, VERTICAL }

  public void fillBorder(BorderType borderType) {
    ClickableItem item = EMPTY_PANE();

    switch (borderType) {
      case TOP -> {
        for (int slot = 0; slot < 9; slot++) setItemAt(slot, item);
      }
      case BOTTOM -> {
        for (int slot = this.inventory.getSize() - 9; slot < this.inventory.getSize(); slot++) setItemAt(slot, item);
      }
      case LEFT -> {
        for (int slot = 0; slot < this.inventory.getSize(); slot += 9) setItemAt(slot, item);
      }
      case RIGHT -> {
        for (int slot = 8; slot < this.inventory.getSize(); slot += 9) setItemAt(slot, item);
      }
      case ALL -> {
        fillBorder(BorderType.TOP);
        fillBorder(BorderType.BOTTOM);
        fillBorder(BorderType.LEFT);
        fillBorder(BorderType.RIGHT);
      }
    }
  }

  public void fillBorder(int location, Direction direction) {
    ClickableItem item = EMPTY_PANE();
    location--;

    switch (direction) {
      case HORIZONTAL -> {
        for (int slot = location * 9; slot < (location * 9) + 9; slot++) setItemAt(slot, item);
      }
      case VERTICAL -> {
        for (int slot = location % 9; slot < this.inventory.getSize(); slot += 9) setItemAt(slot, item);
      }
    }
  }
}
