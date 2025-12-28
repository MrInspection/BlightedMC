package fr.moussax.blightedMC.smp.core.items.crafting.menu;

import fr.moussax.blightedMC.smp.core.shared.menu.interaction.MenuElementPreset;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftingTableMenu {

    public static Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(null, 54, "§rCraft Items");
        for (int slot = 0; slot < 10; slot++) inv.setItem(slot, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());

        inv.setItem(13, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(14, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(15, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(16, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(17, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(18, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(22, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(23, RECIPE_REQUIRED());
        inv.setItem(24, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(25, new ItemBuilder(Material.KNOWLEDGE_BOOK, "§6Crafting Recipes")
            .addLore("§7A tainted book that holds", "§7secrets of §5blighted §7items.", "", "§eClick to view!")
            .toItemStack()
        );
        inv.setItem(26, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(27, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(31, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(32, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(33, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());
        inv.setItem(34, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());

        for (int slot = 35; slot < 45; slot++) inv.setItem(slot, MenuElementPreset.EMPTY_SLOT_FILLER.getItem());

        for (int slot = 45; slot < 49; slot++) {
            inv.setItem(slot, VALID_RECIPE_INDICATOR(false));
        }
        for (int slot = 50; slot < 54; slot++) {
            inv.setItem(slot, VALID_RECIPE_INDICATOR(false));
        }

        inv.setItem(49, MenuElementPreset.CLOSE_BUTTON.getItem());
        return inv;
    }

    public static ItemStack VALID_RECIPE_INDICATOR(boolean isValid) {
        return new ItemBuilder(isValid ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE, "§r").setHideTooltip(true).toItemStack();
    }

    public static ItemStack RECIPE_REQUIRED() {
        return new ItemBuilder(Material.BARRIER, "§cRecipe Required")
            .addLore(
                "§7Add items for a valid recipe in",
                "§7the crafting grid to the left."
            ).toItemStack();
    }
}
