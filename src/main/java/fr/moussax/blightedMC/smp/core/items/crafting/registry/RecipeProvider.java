package fr.moussax.blightedMC.smp.core.items.crafting.registry;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.builder.ShapedRecipeBuilder;
import fr.moussax.blightedMC.smp.core.items.crafting.builder.ShapelessRecipeBuilder;

import java.util.List;

/**
 * Declares a module responsible for providing custom crafting recipes.
 * <p>
 * Implementations define their recipes in {@link #register()} using either
 * direct {@link BlightedRecipe} instances or the fluent builder helpers
 * exposed by this interface.
 */
@FunctionalInterface
public interface RecipeProvider {

    /**
     * Instantiates and registers recipes.
     * <p>
     * Invoked automatically during plugin initialization.
     */
    void register();

    /**
     * Registers a single recipe.
     *
     * @param recipe the recipe to register
     */
    default void add(BlightedRecipe recipe) {
        RecipeRegistry.registerRecipe(recipe);
    }

    /**
     * Registers multiple recipes.
     *
     * @param recipes the recipes to register
     */
    default void add(BlightedRecipe... recipes) {
        for (BlightedRecipe recipe : recipes) {
            RecipeRegistry.registerRecipe(recipe);
        }
    }

    /**
     * Registers all recipes in the given list.
     *
     * @param recipes the recipes to register
     */
    default void add(List<BlightedRecipe> recipes) {
        recipes.forEach(RecipeRegistry::registerRecipe);
    }

    /**
     * Creates a shaped recipe builder for the given result.
     * <p>
     * Intended for use within {@link #register()} to fluently define
     * shaped crafting recipes.
     *
     * @param result the resulting item
     * @param amount the quantity produced
     * @return a shaped recipe builder
     */
    default ShapedRecipeBuilder shapedRecipe(BlightedItem result, int amount) {
        return ShapedRecipeBuilder.of(result, amount);
    }

    /**
     * Creates a shaped recipe builder using a result item ID.
     *
     * @param resultId the result item identifier
     * @param amount   the quantity produced
     * @return a shaped recipe builder
     */
    default ShapedRecipeBuilder shapedRecipe(String resultId, int amount) {
        return ShapedRecipeBuilder.of(resultId, amount);
    }

    /**
     * Creates a shapeless recipe builder for the given result.
     * <p>
     * Intended for use within {@link #register()} to fluently define
     * shapeless crafting recipes.
     *
     * @param result the resulting item
     * @return a shapeless recipe builder
     */
    default ShapelessRecipeBuilder shapelessRecipe(BlightedItem result) {
        return ShapelessRecipeBuilder.of(result);
    }

    /**
     * Creates a shapeless recipe builder using a result item ID.
     *
     * @param resultId the result item identifier
     * @return a shapeless recipe builder
     */
    default ShapelessRecipeBuilder shapelessRecipe(String resultId) {
        return ShapelessRecipeBuilder.of(resultId);
    }
}
