package fr.moussax.blightedMC.core.items;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a generator capable of producing a Bukkit {@link ItemStack}.
 * <p>
 * Implementations define how a specific item should be instantiated and configured,
 * typically used by item templates or factories to create consistent custom items.
 */
public interface ItemGenerator {

  /**
   * Creates and returns a new {@link ItemStack} instance.
   *
   * @return a newly generated item stack
   */
  ItemStack createItemStack();
}
