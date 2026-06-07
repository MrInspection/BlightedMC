package fr.moussax.blightedMC.shared.ui.menu.interaction;

import fr.moussax.blightedMC.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public enum MenuElementPreset {
    CLOSE_BUTTON(new ItemBuilder(Material.BARRIER, "§cClose").toItemStack()),
    BACK_BUTTON(new ItemBuilder(Material.ARROW, "§aGo Back").toItemStack()),
    NEXT_BUTTON(new ItemBuilder(Material.ARROW, "§aNext Page").toItemStack()),
    EMPTY_SLOT_FILLER(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, "§r").hideTooltip().toItemStack());

    private final ItemStack item;

    MenuElementPreset(ItemStack item) {
        this.item = item;
    }
}
