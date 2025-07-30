package fr.moussax.blightedMC.core.items.crafting.menu;

import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftingTableMenu {

  public static Inventory createInventory() {
    Inventory inv = Bukkit.createInventory(null, 54, "§rCraft Items");
    for (int slot = 0; slot < 10; slot++) inv.setItem(slot, EMPTY_PANE());

    inv.setItem(13, EMPTY_PANE());
    inv.setItem(14, EMPTY_PANE());
    inv.setItem(15, EMPTY_PANE());
    inv.setItem(16, EMPTY_PANE());
    inv.setItem(17, EMPTY_PANE());
    inv.setItem(18, EMPTY_PANE());
    inv.setItem(22, EMPTY_PANE());
    inv.setItem(23, RECIPE_REQUIRED());

    inv.setItem(24, EMPTY_PANE());
    inv.setItem(25, EMPTY_PANE());
    inv.setItem(26, EMPTY_PANE());
    inv.setItem(27, EMPTY_PANE());
    inv.setItem(31, EMPTY_PANE());
    inv.setItem(32, EMPTY_PANE());
    inv.setItem(33, EMPTY_PANE());
    inv.setItem(34, EMPTY_PANE());

    for (int slot = 35; slot < 45; slot++) inv.setItem(slot, EMPTY_PANE());
    for (int slot = 45; slot < 54; slot++) inv.setItem(slot, VALID_RECIPE_INDICATOR());

    inv.setItem(49, CLOSE_MENU());
    return inv;
  }

  private static ItemStack EMPTY_PANE() {
    return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, " ").toItemStack();
  }

  private static ItemStack VALID_RECIPE_INDICATOR() {
    return new ItemBuilder(Material.RED_STAINED_GLASS_PANE, "§r").toItemStack();
  }

  public static ItemStack RECIPE_REQUIRED() {
    return new ItemBuilder(Material.BARRIER, "§cRecipe Required")
        .addLore(
            "§7Add items for a valid recipe in",
            "§7the crafting grid to the left."
        ).toItemStack();
  }

  public static ItemStack CLOSE_MENU() {
    return new ItemBuilder(Material.BARRIER, "§cClose").toItemStack();
  }
}
