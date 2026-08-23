package fr.moussax.blightedMC.engine.items.recipes.forging.registry;

import fr.moussax.blightedMC.engine.items.recipes.forging.ForgeRecipe;
import java.util.function.Consumer;

/**
 * Functional interface used to register {@link ForgeRecipe}s with the forge
 * recipe registry.
 *
 * <p>Implementations receive each recipe through {@link #register(ForgeRecipe)},
 * which delegates to the underlying {@link Consumer} implementation.</p>
 */
@FunctionalInterface
public interface ForgeRegistryHandler extends Consumer<ForgeRecipe> {

    /**
     * Registers a forge recipe.
     *
     * @param recipe the forge recipe to register
     */
    default void register(ForgeRecipe recipe) {
        accept(recipe);
    }
}
