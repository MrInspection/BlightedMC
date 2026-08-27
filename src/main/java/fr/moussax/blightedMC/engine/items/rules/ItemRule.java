package fr.moussax.blightedMC.engine.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Defines behavioral restrictions for custom items in BlightedMC.
 *
 * <p>Implementations override these methods to define permissions.
 * Defaults are set to prevent placement but allow interaction.</p>
 */
public interface ItemRule {

    /** Rule preventing bucket fill and empty operations. */
    ItemRule PREVENT_BUCKET_INTERACTIONS = new ItemRule() {
        @Override
        public boolean canUse(Event event, ItemStack itemStack) {
            return event instanceof PlayerBucketEmptyEvent;
        }
    };

    /** Rule preventing item consumption (eating or drinking). */
    ItemRule PREVENT_CONSUME = new ItemRule() {
        @Override
        public boolean canUse(Event event, ItemStack itemStack) {
            return event instanceof PlayerItemConsumeEvent;
        }
    };

    /** Rule preventing items from being dropped by players. */
    ItemRule PREVENT_DROP = new ItemRule() {
        @Override
        public boolean canUse(Event event, ItemStack itemStack) {
            return event instanceof PlayerDropItemEvent;
        }
    };

    /** Rule preventing all environmental interactions with the item. */
    ItemRule PREVENT_INTERACTION = new ItemRule() {
        @Override
        public boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
            return false;
        }
    };

    /** Rule preventing block placement and block right-click interactions. */
    ItemRule PREVENT_PLACEMENT = new ItemRule() {
        @Override
        public boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
            return true; // FORBIDDEN
        }

        @Override
        public boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
            return event.getAction() != Action.RIGHT_CLICK_BLOCK;
        }
    };

    /** Rule preventing projectile launching (bows, tridents, snowballs). */
    ItemRule PREVENT_PROJECTILE_LAUNCH = new ItemRule() {
        @Override
        public boolean canUse(Event event, ItemStack itemStack) {
            return event instanceof ProjectileLaunchEvent;
        }
    };

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
