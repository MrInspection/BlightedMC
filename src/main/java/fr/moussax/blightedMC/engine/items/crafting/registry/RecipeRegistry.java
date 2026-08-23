package fr.moussax.blightedMC.engine.items.crafting.registry;

import fr.moussax.blightedMC.content.recipes.EndRecipes;
import fr.moussax.blightedMC.content.recipes.EquipmentRecipes;
import fr.moussax.blightedMC.content.recipes.MaterialRecipes;
import fr.moussax.blightedMC.content.recipes.NetherMaterialRecipes;
import fr.moussax.blightedMC.engine.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.engine.items.crafting.builder.ShapedRecipeBuilder;
import fr.moussax.blightedMC.engine.items.crafting.builder.ShapelessRecipeBuilder;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Central registry for custom {@link BlightedRecipe} definitions.
 *
 * <p>Recipes are provided by registered {@link RegistryModule} implementations
 * and added to the global recipe collection when {@link #initialize()} is
 * called. The registry also provides factory methods for creating shaped and
 * shapeless recipe builders.</p>
 */
public final class RecipeRegistry {

    private static final List<RegistryModule<RecipeRegistryHandler>> PROVIDERS = List.of(
        new MaterialRecipes(),
        new NetherMaterialRecipes(),
        new EndRecipes(),
        new EquipmentRecipes()
    );

    private RecipeRegistry() {
    }

    /**
     * Initializes the recipe registry.
     *
     * <p>Previously registered recipes are cleared before all configured
     * recipe providers are loaded.</p>
     */
    public static void initialize() {
        clear();
        PROVIDERS.forEach(module -> module.register(RecipeRegistry::register));
        Log.success("RecipesRegistry", "Registered " + BlightedRecipe.REGISTERED_RECIPES.size() + " custom recipes.");
    }

    /**
     * Registers a custom recipe.
     *
     * @param recipe the recipe to register
     */
    public static void register(@NonNull BlightedRecipe recipe) {
        recipe.addRecipe();
    }

    /**
     * Registers multiple custom recipes.
     *
     * @param recipes the recipes to register
     */
    public static void register(BlightedRecipe... recipes) {
        for (BlightedRecipe recipe : recipes) {
            register(recipe);
        }
    }

    /**
     * Creates a builder for a shaped custom recipe.
     *
     * @param resultId the item ID of the recipe result
     * @param amount   the amount produced by the recipe
     * @return a shaped recipe builder
     */
    public static ShapedRecipeBuilder shapedRecipe(String resultId, int amount) {
        return ShapedRecipeBuilder.of(resultId, amount);
    }

    /**
     * Creates a builder for a shapeless custom recipe.
     *
     * @param resultId the item ID of the recipe result
     * @param amount   the amount produced by the recipe
     * @return a shapeless recipe builder
     */
    public static ShapelessRecipeBuilder shapelessRecipe(String resultId, int amount) {
        return ShapelessRecipeBuilder.of(resultId);
    }

    /**
     * Removes all recipes currently registered in the recipe registry.
     */
    public static void clear() {
        BlightedRecipe.REGISTERED_RECIPES.clear();
    }
}
