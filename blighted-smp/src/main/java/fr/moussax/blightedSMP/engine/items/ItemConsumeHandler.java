package fr.moussax.blightedSMP.engine.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Functional callback invoked when a custom {@link BlightedItem} is consumed by a player.
 */
@FunctionalInterface
public interface ItemConsumeHandler {

    /**
     * Invoked when a custom item is consumed.
     *
     * @param player    player consuming the item
     * @param itemStack item stack being consumed
     */
    void onConsume(Player player, ItemStack itemStack);
}
