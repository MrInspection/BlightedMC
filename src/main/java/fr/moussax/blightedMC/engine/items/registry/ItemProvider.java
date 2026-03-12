package fr.moussax.blightedMC.engine.items.registry;

import fr.moussax.blightedMC.engine.items.BlightedItem;

import java.util.List;

/**
 * Defines a provider that supplies custom items to the {@link ItemRegistry}.
 * <p>
 * Implementations should use the {@link #register()} method to instantiate
 * and add their items using the provided default helper methods.
 */
@FunctionalInterface
public interface ItemProvider {

    /**
     * Logic to instantiate and register items.
     * Called automatically during plugin initialization.
     */
    void register();

    /**
     * Registers a single item with the directory.
     * <p>
     * Serves as a public façade over the package-private
     * {@link ItemRegistry#addItem(BlightedItem)} method.
     *
     * @param item the item to register
     */
    default void add(BlightedItem item) {
        ItemRegistry.addItem(item);
    }

    /**
     * Registers multiple items in a single call.
     *
     * @param items the items to register
     */
    default void add(BlightedItem... items) {
        for (BlightedItem item : items) {
            ItemRegistry.addItem(item);
        }
    }

    /**
     * Registers all items contained in the given list.
     *
     * @param items the items to register
     */
    default void add(List<BlightedItem> items) {
        items.forEach(ItemRegistry::addItem);
    }
}
