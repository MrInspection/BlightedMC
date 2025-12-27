package fr.moussax.blightedMC.smp.core.items.rules;

import fr.moussax.blightedMC.smp.core.items.rules.common.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Defines behavioral restrictions for custom items in BlightedMC.
 * <p>
 * Implementations override these methods to define permissions.
 * Defaults are set to prevent placement but allow interaction.
 */
public interface ItemRule {
    ItemRule PREVENT_BUCKET_INTERACTIONS = new PreventBucketInteractionsRule();
    ItemRule PREVENT_CONSUME = new PreventConsumeRule();
    ItemRule PREVENT_DROP = new PreventDropRule();
    ItemRule PREVENT_INTERACTION = new PreventInteractionRule();
    ItemRule PREVENT_PLACEMENT = new PreventPlacementRule();
    ItemRule PREVENT_PROJECTILE_LAUNCH = new PreventProjectileLaunchRule();

    /**
     * Determines whether the item can be placed as a block.
     * <p>
     * <b>Default:</b> {@code false} (Prevents custom items like enchanted blocks from being placed)
     *
     * @param event     the {@link BlockPlaceEvent}
     * @param itemStack the item being placed
     * @return {@code true} to allow placement, {@code false} to cancel it
     */
    default boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
        return false;
    }

    /**
     * Determines whether the item can interact with the environment.
     * <p>
     * <b>Default:</b> {@code true} (Allows standard clicking/interaction)
     *
     * @param event     the {@link PlayerInteractEvent}
     * @param itemStack the item being used
     * @return {@code true} to allow interaction, {@code false} to prevent it
     */
    default boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
        return true;
    }

    /**
     * Determines whether the item can be used in generic contexts.
     *
     * @param event     the triggering {@link Event}
     * @param itemStack the item being used
     * @return {@code true} if allowed, {@code false} otherwise
     */
    default boolean canUse(Event event, ItemStack itemStack) {
        return false;
    }
}
