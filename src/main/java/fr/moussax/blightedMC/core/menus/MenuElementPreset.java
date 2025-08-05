package fr.moussax.blightedMC.core.menus;

import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MenuElementPreset {
  CLOSE_BUTTON(new ItemBuilder(Material.BARRIER, "§Close").toItemStack()),
  BACK_BUTTON(new ItemBuilder(Material.ARROW, "§6Go Back").toItemStack()),
  NEXT_BUTTON(new ItemBuilder(Material.ARROW, "§6Next Page").toItemStack()),
  EMPTY_SLOT_FILLER(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, "§r").hideTooltip(true).toItemStack());

  private final ItemStack item;

  MenuElementPreset(ItemStack item) {
    this.item = item;
  }

  public ItemStack getItem() {
    return item;
  }
}
