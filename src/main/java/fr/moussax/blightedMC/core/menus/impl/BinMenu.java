package fr.moussax.blightedMC.core.menus.impl;

import fr.moussax.blightedMC.core.menus.ClickableItem;
import fr.moussax.blightedMC.core.menus.Menu;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;

public class BinMenu extends Menu {
  private static final int size = 9 * 6;
  private static final int[] NON_EMPTY_SLOTS = {
      0, 1, 2, 3, 4, 5, 6, 7, 8,
      9,                              17,
      18,                             26,
      27,                             35,
      36,                             44,
      45, 46, 47, 48, 49, 50, 51, 52, 53
  };

  public BinMenu() {
    super("Trash", size);
  }

  @Override
  public void initializeItems(Player player) {
    this.fillBorder(BorderType.ALL);
    this.setItemAt(8,6, getClearTrashButton());
    this.setItemAt(5, 6, CLOSE_BUTTON());
  }

  private ClickableItem getClearTrashButton() {
    ItemBuilder item = new ItemBuilder(Material.CAULDRON, "§cClear Trash");
    return new ClickableItem(item.toItemStack(), player -> this.clearInventory());
  }

  private void clearInventory() {
    Set<Integer> nonEmptySlotsSet = new HashSet<>();
    for (int slot : NON_EMPTY_SLOTS) {
      nonEmptySlotsSet.add(slot);
    }

    for (int i = 0; i < size; i++) {
      if (!nonEmptySlotsSet.contains(i)) {
        this.getInventory().clear(i);
      }
    }
  }

  @Override
  public Inventory getInventory() {
    return this.inventory;
  }
}
