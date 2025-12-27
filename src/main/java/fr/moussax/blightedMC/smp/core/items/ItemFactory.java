package fr.moussax.blightedMC.smp.core.items;

import org.bukkit.inventory.ItemStack;

/**
 * Functional contract for creating {@link ItemStack} instances.
 * <p>
 * Implementations must return a fully configured, new item on each call.
 */
@FunctionalInterface
public interface ItemFactory {

    /**
     * Creates a new {@link ItemStack}.
     *
     * @return a newly created item stack
     */
    ItemStack createItemStack();
}
