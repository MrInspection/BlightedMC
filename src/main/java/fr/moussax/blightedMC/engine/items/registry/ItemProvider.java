package fr.moussax.blightedMC.engine.items.registry;

import fr.moussax.blightedMC.engine.items.BlightedItem;

import java.util.List;

@FunctionalInterface
public interface ItemProvider {

    void register();

    default void add(BlightedItem item) {
        ItemRegistry.addItem(item);
    }

    default void add(BlightedItem... items) {
        for (BlightedItem item : items) {
            ItemRegistry.addItem(item);
        }
    }

    default void add(List<BlightedItem> items) {
        items.forEach(ItemRegistry::addItem);
    }
}
