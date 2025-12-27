package fr.moussax.blightedMC.smp.core.items.forging.registry;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.forging.ForgeRecipe;

import java.util.List;

/**
 * Defines a module that provides forge recipes to the {@link ForgeRegistry}.
 * <p>
 * Implementations of this interface should define their recipes in
 * {@link #register()} and can register them using the provided {@code add}
 * helper methods. Builders for creating new recipes are also exposed
 * via {@link #forgeRecipe}.
 */
@FunctionalInterface
public interface ForgeProvider {

    /**
     * Invoked to register all forge recipes defined by this provider.
     * Implementations should call {@link #add} or use {@link #forgeRecipe}.
     */
    void register();

    /**
     * Registers a single forge recipe.
     *
     * @param recipe the recipe to register
     */
    default void add(ForgeRecipe recipe) {
        ForgeRegistry.register(recipe);
    }

    /**
     * Registers multiple forge recipes.
     *
     * @param recipes the recipes to register
     */
    default void add(ForgeRecipe... recipes) {
        for (ForgeRecipe recipe : recipes) {
            ForgeRegistry.register(recipe);
        }
    }

    /**
     * Registers a list of forge recipes.
     *
     * @param recipes the list of recipes to register
     */
    default void add(List<ForgeRecipe> recipes) {
        recipes.forEach(ForgeRegistry::register);
    }

    /**
     * Creates a new builder for a forge recipe with the given result item.
     *
     * @param result the item produced
     * @param amount the quantity produced
     * @return a new builder instance
     */
    default ForgeRecipe.Builder forgeRecipe(BlightedItem result, int amount) {
        return ForgeRecipe.Builder.of(result, amount);
    }

    /**
     * Creates a new builder for a forge recipe using the result item's ID.
     *
     * @param resultId the ID of the item produced
     * @param amount   the quantity produced
     * @return a new builder instance
     */
    default ForgeRecipe.Builder forgeRecipe(String resultId, int amount) {
        return ForgeRecipe.Builder.of(resultId, amount);
    }
}
