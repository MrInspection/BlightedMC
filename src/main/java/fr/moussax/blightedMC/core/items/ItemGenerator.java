package fr.moussax.blightedMC.core.items;

import org.bukkit.inventory.ItemStack;

public interface ItemGenerator {
  /**
   * Creates and returns a new ItemStack instance.
   *
   * @return a generated ItemStack
   */
  ItemStack createItemStack();
}
