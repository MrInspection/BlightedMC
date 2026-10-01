package fr.moussax.bedrock.ui.menu.interaction;

import fr.moussax.bedrock.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Provides predefined visual elements for common menu interactions.
 */
@Getter
public enum MenuElementPreset {
    CLOSE_BUTTON(new ItemBuilder(Material.BARRIER, "§cClose").toItemStack()),
    BACK_BUTTON(new ItemBuilder(Material.ARROW, "§aGo Back").toItemStack()),
    PREVIOUS_BUTTON(new ItemBuilder(Material.ARROW, "§aPrevious Page").toItemStack()),
    NEXT_BUTTON(new ItemBuilder(Material.ARROW, "§aNext Page").toItemStack()),
    EMPTY_SLOT_FILLER(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, "§r").hideTooltip().toItemStack());

    private final ItemStack item;

    /**
     * Creates a menu element preset.
     *
     * @param item item displayed by this preset
     */
    MenuElementPreset(ItemStack item) {
        this.item = item;
    }

    /**
     * Returns an isolated clone of the item representing this preset.
     *
     * @return cloned item stack
     */
    public ItemStack getItem() {
        return item.clone();
    }
}
