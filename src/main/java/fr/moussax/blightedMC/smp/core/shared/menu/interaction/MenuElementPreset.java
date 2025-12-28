package fr.moussax.blightedMC.smp.core.shared.menu.interaction;

import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MenuElementPreset {
    CLOSE_BUTTON(new ItemBuilder(Material.BARRIER, "§cClose").toItemStack()),
    BACK_BUTTON(new ItemBuilder(Material.ARROW, "§aGo Back").toItemStack()),
    NEXT_BUTTON(new ItemBuilder(Material.ARROW, "§aNext Page").toItemStack()),
    EMPTY_SLOT_FILLER(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, "§r").setHideTooltip().toItemStack());

    private final ItemStack item;

    MenuElementPreset(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }
}
