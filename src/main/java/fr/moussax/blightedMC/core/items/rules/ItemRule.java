package fr.moussax.blightedMC.core.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a rule that defines the conditions under which a custom item can be used.
 * <p>
 * Implementations can override the default methods to restrict placing, interacting,
 * or using specific items in the game.
 */
public interface ItemRule {

  /**
   * Checks whether the specified item can be placed as a block.
   *
   * @param event     the {@link BlockPlaceEvent} triggered by the placement
   * @param itemStack the item stack being placed
   * @return {@code true} if the item can be placed, {@code false} to cancel placement
   */
  default boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
    return true;
  }

  /**
   * Checks whether the specified item can interact with the world (right/left click).
   *
   * @param event     the {@link PlayerInteractEvent} triggered by the interaction
   * @param itemStack the item stack used for interaction
   * @return {@code true} if the interaction is allowed, {@code false} to prevent it
   */
  default boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
    return true;
  }

  /**
   * Checks whether the specified item can be used in a general context.
   * <p>
   * This is a generic fallback rule for other item-related events that are not
   * block placement or direct interaction.
   *
   * @param event     the {@link Event} representing the item usage context
   * @param itemStack the item stack being used
   * @return {@code true} if the item can be used, {@code false} otherwise
   */
  default boolean canUse(Event event, ItemStack itemStack) {
    return true;
  }
}
