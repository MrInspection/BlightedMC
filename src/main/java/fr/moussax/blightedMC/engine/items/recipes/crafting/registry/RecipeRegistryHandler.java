package fr.moussax.blightedMC.engine.items.recipes.crafting.registry;

import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedRecipe;
import java.util.function.Consumer;

/**
 * Functional interface used to register {@link BlightedRecipe}s with the
 * recipe registry.
 *
 * <p>Extends {@link Consumer} so recipe registry modules can provide recipes
 * through method references or lambda expressions.</p>
 */
@FunctionalInterface
public interface RecipeRegistryHandler extends Consumer<BlightedRecipe> {

    /**
     * Registers a recipe.
     *
     * @param recipe the recipe to register
     */
    default void register(BlightedRecipe recipe) {
        accept(recipe);
    }
}
