package fr.moussax.blightedMC.engine.items.rules;

import org.bukkit.event.Event;
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
 * <p>Implementations override these methods to restrict specific actions.
 * All rule methods return {@code true} to restrict/cancel an action, or {@code false} to permit it.</p>
 */
public interface ItemRule {

    /**
     * Rule preventing bucket fill and empty operations.
     */
    ItemRule PREVENT_BUCKET_INTERACTIONS = new ItemRule() {
        @Override
        public boolean shouldRestrictUse(Event event, ItemStack itemStack) {
            return event instanceof PlayerBucketEmptyEvent;
        }
    };

    /**
     * Rule preventing item consumption (eating or drinking).
     */
    ItemRule PREVENT_CONSUME = new ItemRule() {
        @Override
        public boolean shouldRestrictUse(Event event, ItemStack itemStack) {
            return event instanceof PlayerItemConsumeEvent;
        }
    };

    /**
     * Rule preventing items from being dropped by players.
     */
    ItemRule PREVENT_DROP = new ItemRule() {
        @Override
        public boolean shouldRestrictUse(Event event, ItemStack itemStack) {
            return event instanceof PlayerDropItemEvent;
        }
    };

    /**
     * Rule preventing all environmental interactions with the item.
     */
    ItemRule PREVENT_INTERACTION = new ItemRule() {
        @Override
        public boolean shouldRestrictInteract(PlayerInteractEvent event, ItemStack itemStack) {
            return true;
        }
    };

    /**
     * Rule preventing custom item block placement.
     */
    ItemRule PREVENT_PLACEMENT = new ItemRule() {
        @Override
        public boolean shouldRestrictPlace(BlockPlaceEvent event, ItemStack itemStack) {
            return true;
        }
    };

    /**
     * Rule preventing projectile launching (bows, tridents, snowballs).
     */
    ItemRule PREVENT_PROJECTILE_LAUNCH = new ItemRule() {
        @Override
        public boolean shouldRestrictUse(Event event, ItemStack itemStack) {
            return event instanceof ProjectileLaunchEvent;
        }
    };

    /**
     * Determines whether block placement should be restricted for this item.
     *
     * @param event     block place event
     * @param itemStack item stack being placed
     * @return {@code true} to restrict placement (cancel event), {@code false} to allow
     */
    default boolean shouldRestrictPlace(BlockPlaceEvent event, ItemStack itemStack) {
        return false;
    }

    /**
     * Determines whether player interaction should be restricted for this item.
     *
     * @param event     player interact event
     * @param itemStack item stack being used
     * @return {@code true} to restrict interaction (cancel event), {@code false} to allow
     */
    default boolean shouldRestrictInteract(PlayerInteractEvent event, ItemStack itemStack) {
        return false;
    }

    /**
     * Determines whether generic usage should be restricted for this item.
     *
     * @param event     triggering event
     * @param itemStack item stack being used
     * @return {@code true} to restrict usage (cancel event), {@code false} to allow
     */
    default boolean shouldRestrictUse(Event event, ItemStack itemStack) {
        return false;
    }
}
