package fr.moussax.blightedMC.engine.items.registry;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import java.util.function.Consumer;

/**
 * Functional interface used to register {@link BlightedItem}s with an item registry.
 *
 * <p>Extends {@link Consumer} so registry modules can provide items through
 * method references or lambda expressions.</p>
 */
@FunctionalInterface
public interface ItemRegistryHandler extends Consumer<BlightedItem> {

    /**
     * Registers a {@link BlightedItem}.
     *
     * @param item the item to register
     */
    default void register(BlightedItem item) {
        accept(item);
    }
}
