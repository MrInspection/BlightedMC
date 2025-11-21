package fr.moussax.blightedMC.core.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Defines behavioral restrictions for custom items in BlightedMC.
 * <p>
 * Implementations of this interface allow developers to control when and how
 * a specific item can be placed, interacted with, or used in events.
 * <p>
 * All methods have permissive defaults ({@code true}) and can be selectively overridden.
 */
public interface ItemRule {

    /**
     * Determines whether the item can be placed as a block.
     *
     * @param event     the {@link BlockPlaceEvent} representing the placement action
     * @param itemStack the item being placed
     * @return {@code true} to allow placement, {@code false} to cancel it
     */
    default boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
        return false;
    }

    /**
     * Determines whether the item can interact with the environment,
     * typically triggered by right or left-click actions.
     *
     * @param event     the {@link PlayerInteractEvent} representing the interaction
     * @param itemStack the item being used to interact
     * @return {@code true} to allow interaction, {@code false} to prevent it
     */
    default boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
        return true;
    }

    /**
     * Determines whether the item can be used in a general event context.
     * <p>
     * This applies to non-standard interactions, such as custom plugin events
     * or indirect uses not covered by placement or interaction rules.
     *
     * @param event     the {@link Event} representing the usage context
     * @param itemStack the item being used
     * @return {@code true} to allow usage, {@code false} otherwise
     */
    default boolean canUse(Event event, ItemStack itemStack) {
        return false;
    }
}
