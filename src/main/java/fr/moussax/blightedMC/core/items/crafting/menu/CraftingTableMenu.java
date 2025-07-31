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
    inv.setItem(16, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, "§c???").toItemStack());
    inv.setItem(17, EMPTY_PANE());
    inv.setItem(18, EMPTY_PANE());
    inv.setItem(22, EMPTY_PANE());
    inv.setItem(23, RECIPE_REQUIRED());

    inv.setItem(24, EMPTY_PANE());
    inv.setItem(25, new ItemBuilder(Material.KNOWLEDGE_BOOK, "§aRecipe Book").addLore("§eClick to view!").toItemStack());
    inv.setItem(26, EMPTY_PANE());
    inv.setItem(27, EMPTY_PANE());
    inv.setItem(31, EMPTY_PANE());
    inv.setItem(32, EMPTY_PANE());
    inv.setItem(33, EMPTY_PANE());
    inv.setItem(34, BLIGHTED_FAVORS());

    for (int slot = 35; slot < 45; slot++) inv.setItem(slot, EMPTY_PANE());

    for (int slot = 45; slot < 49; slot++) {
      inv.setItem(slot, VALID_RECIPE_INDICATOR(false));
    }
    for (int slot = 50; slot < 54; slot++) {
      inv.setItem(slot, VALID_RECIPE_INDICATOR(false));
    }

    inv.setItem(49, CLOSE_MENU());
    return inv;
  }

  private static ItemStack BLIGHTED_FAVORS() {
    return new ItemBuilder(Material.PLAYER_HEAD, "§5Blighted Favors")
            .addLore("§7Favors: §50", " ",
                "§8 Favors are §5Extraordinary drops §8from ",
                "§8 hostile mobs that can be used to craft ",
                "§8 & forge §5powerful items§8, summon §5bosses§8 ",
                "§8 in the §dBlighted Terminal §8and more. ",
                " ",
                "§7 Drop Chance: §53% §8(Extraordinary)", " "
            )
        .setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDg4NmUwZjQxMTg1YjE4YTNhZmQ4OTQ4OGQyZWU0Y2FhMDczNTAwOTI0N2NjY2YwMzljZWQ2YWVkNzUyZmYxYSJ9fX0=").toItemStack();
  }

  private static ItemStack EMPTY_PANE() {
    return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, "§r").hideTooltip(true).toItemStack();
  }

  public static ItemStack VALID_RECIPE_INDICATOR(boolean isValid) {
    Material paneMaterial = isValid ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
    return new ItemBuilder(paneMaterial, "§r").hideTooltip(true).toItemStack();
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
