package fr.moussax.blightedMC.engine.items.forging.registry;

import fr.moussax.blightedMC.engine.items.forging.ForgeRecipe;
import java.util.function.Consumer;

@FunctionalInterface
public interface ForgeRegistryHandler extends Consumer<ForgeRecipe> {
    default void register(ForgeRecipe recipe) {
        accept(recipe);
    }
}
