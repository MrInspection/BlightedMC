package fr.moussax.blightedMC.engine.items.registry;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import java.util.function.Consumer;

@FunctionalInterface
public interface ItemRegistryHandler extends Consumer<BlightedItem> {
    default void register(BlightedItem item) {
        accept(item);
    }
}
