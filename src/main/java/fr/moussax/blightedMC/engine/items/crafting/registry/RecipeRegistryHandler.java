package fr.moussax.blightedMC.engine.items.crafting.registry;

import fr.moussax.blightedMC.engine.items.crafting.BlightedRecipe;
import java.util.function.Consumer;

@FunctionalInterface
public interface RecipeRegistryHandler extends Consumer<BlightedRecipe> {
    default void register(BlightedRecipe recipe) {
        accept(recipe);
    }
}
