package fr.moussax.blightedMC.core.menus;

import org.bukkit.inventory.InventoryHolder;

public interface BlightedInventory extends InventoryHolder {
  ClickableItem getItemAt(int slot);
}
