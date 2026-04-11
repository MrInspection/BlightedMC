package fr.moussax.blightedMC.engine.items.crafting.registry;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.engine.items.crafting.builder.ShapedRecipeBuilder;
import fr.moussax.blightedMC.engine.items.crafting.builder.ShapelessRecipeBuilder;

import java.util.List;

@FunctionalInterface
public interface RecipeProvider {

    void register();

    default void add(BlightedRecipe recipe) {
        RecipeRegistry.registerRecipe(recipe);
    }

    default void add(BlightedRecipe... recipes) {
        for (BlightedRecipe recipe : recipes) {
            RecipeRegistry.registerRecipe(recipe);
        }
    }

    default void add(List<BlightedRecipe> recipes) {
        recipes.forEach(RecipeRegistry::registerRecipe);
    }

    default ShapedRecipeBuilder shapedRecipe(BlightedItem result, int amount) {
        return ShapedRecipeBuilder.of(result, amount);
    }

    default ShapedRecipeBuilder shapedRecipe(String resultId, int amount) {
        return ShapedRecipeBuilder.of(resultId, amount);
    }

    default ShapelessRecipeBuilder shapelessRecipe(BlightedItem result) {
        return ShapelessRecipeBuilder.of(result);
    }

    default ShapelessRecipeBuilder shapelessRecipe(String resultId) {
        return ShapelessRecipeBuilder.of(resultId);
    }
}
