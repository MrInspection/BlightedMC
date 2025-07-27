package fr.moussax.blightedMC.core.menus;

import fr.moussax.blightedMC.core.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Menu implements BlightedInventory {

  public static ClickableItem EMPTY_PANE() {
    ItemBuilder emptyPane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1, " ");
    return new ClickableItem(emptyPane.toItemStack(), player -> {
    });
  }

  public static ClickableItem BACK_BUTTON() {
    ItemBuilder backButton = new ItemBuilder(Material.ARROW, 1, "§cBack");
    return new ClickableItem(backButton.toItemStack(), MenuRouter::goBack);
  }

  public static ClickableItem NEXT_PAGE_BUTTON(Consumer<Player> onClick) {
    ItemBuilder nextButton = new ItemBuilder(Material.ARROW, 1, "§aNext Page");
    return new ClickableItem(nextButton.toItemStack(), onClick);
  }

  public static ClickableItem CLOSE_BUTTON() {
    ItemBuilder closeButton = new ItemBuilder(Material.BARRIER, 1, "§cClose");
    return new ClickableItem(closeButton.toItemStack(), MenuRouter::closeCurrentMenu);
  }

  protected Inventory inventory;
  protected int size;
  protected String title;
  protected Map<Integer, ClickableItem> clickableItems = new HashMap<>();

  int incrementalSlot = 0;

  public abstract void initializeItems(Player player);

  public Menu(String title, int size) {
    this.size = size;
    this.title = title;
    this.inventory = Bukkit.createInventory((InventoryHolder) this, size, title);
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
    this.setItemAt((x - 1) + ((y - 1) * 9), item);
  }

  public void setNextItem(ClickableItem item) {
    if (incrementalSlot >= this.inventory.getSize()) {
      throw new IndexOutOfBoundsException("The inventory is full!");
    }

    while (this.clickableItems.containsKey(incrementalSlot)) {
      incrementalSlot++;
      if (incrementalSlot >= this.inventory.getSize()) {
        throw new IndexOutOfBoundsException("The inventory is full!");
      }
    }

    this.clickableItems.put(incrementalSlot, item);
    this.inventory.setItem(incrementalSlot, item.getItem());
    incrementalSlot++;
  }

  @Override
  public ClickableItem getItemAt(int slot) {
    return this.clickableItems.getOrDefault(slot, null);
  }

  public void fillRemaningSlotsWithEmptyPanes() {
    for (int slot = 0; slot < this.inventory.getSize(); slot++) {
      if (!this.clickableItems.containsKey(slot)) {
        this.clickableItems.put(slot, EMPTY_PANE());
      }
    }
  }

  public enum BorderType {TOP, BOTTOM, LEFT, RIGHT, ALL}

  public enum Direction {HORIZONTAL, VERTICAL}

  public void fillBorder(BorderType borderType) {
    ClickableItem item = EMPTY_PANE();

    switch (borderType) {
      case TOP:
        for (int slot = 0; slot < 9; slot++) {
          setItemAt(slot, item);
        }
        break;
      case BOTTOM:
        for (int slot = this.getInventory().getSize() - 9; slot < this.getInventory().getSize(); slot++) {
          setItemAt(slot, item);
        }
        break;
      case LEFT:
        for (int slot = 0; slot < this.inventory.getSize(); slot += 9) {
          setItemAt(slot, item);
        }
        break;
      case RIGHT:
        for (int slot = 8; slot < this.inventory.getSize(); slot += 9) {
          setItemAt(slot, item);
        }
        break;
      case ALL:
        this.fillBorder(BorderType.TOP);
        this.fillBorder(BorderType.BOTTOM);
        this.fillBorder(BorderType.LEFT);
        this.fillBorder(BorderType.RIGHT);
        break;
    }
  }

  public void fillBorder(int location, Direction direction) {
    ClickableItem item = EMPTY_PANE();
    location--;

    switch (direction) {
      case HORIZONTAL -> {
        for (int slot = location * 9; slot < (location * 9) + 9; slot++) {
          setItemAt(slot, item);
        }
      }
      case VERTICAL -> {
        for (int slot = location % 9; slot < this.inventory.getSize(); slot += 9) {
          setItemAt(slot, item);
        }
      }
    }
  }

  /*public static String getTitle(Inventory bukkitInventory) {
    CraftInventoryCustom inventory = (CraftInventoryCustom) bukkitInventory;
    Field titleField;
    try {
      titleField = inventory.getInventory().getClass().getDeclaredField("title");
      titleField.setAccessible(true);
      return (String) titleField.get(inventory.getInventory());
    } catch (NoSuchFieldException | IllegalAccessException ignored) {
    }
    return null;
  }*/
}
