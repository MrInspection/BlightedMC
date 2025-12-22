package fr.moussax.blightedMC.core.items.crafting.registry;

import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.game.recipes.MaterialRecipes;
import fr.moussax.blightedMC.utils.debug.Log;

import java.util.List;

/**
 * Handles the registration and management of all custom crafting recipes
 * in the BlightedMC plugin.
 * <p>
 * This class provides utility methods for:
 * <ul>
 *   <li>Adding shaped and shapeless recipes.</li>
 *   <li>Clearing all registered recipes.</li>
 *   <li>Initializing recipes from predefined categories.</li>
 * </ul>
 * <p>
 * This class cannot be instantiated.
 */
public final class RecipesDirectory {
    private RecipesDirectory() {
    }

    /**
     * Registers a custom recipe in the game.
     *
     * @param recipe the {@link BlightedRecipe} to add
     */
    public static void add(BlightedRecipe recipe) {
        recipe.addRecipe();
    }

    public static void add(BlightedShapedRecipe... recipes) {
        for (BlightedShapedRecipe recipe : recipes) {
            recipe.addRecipe();
        }
    }

    /**
     * Creates and registers a shaped crafting recipe.
     * <p>
     * A shaped recipe requires a specific pattern of ingredients.
     *
     * @param result  the resulting {@link ItemTemplate} item
     * @param amount  the quantity of items to produce
     * @param pattern the list of {@link CraftingObject} representing the crafting grid pattern
     */
    public static void addShapedRecipe(ItemTemplate result, int amount, List<CraftingObject> pattern) {
        BlightedShapedRecipe shaped = new BlightedShapedRecipe(result, amount);
        shaped.setRecipe(pattern);
        add(shaped);
    }

    /**
     * Creates and registers a shapeless crafting recipe.
     * <p>
     * A shapeless recipe allows the ingredients to be placed in any order.
     *
     * @param result      the resulting {@link ItemTemplate} item
     * @param ingredients the list of {@link CraftingObject} ingredients
     */
    public static void addShapelessRecipe(ItemTemplate result, List<CraftingObject> ingredients) {
        BlightedShapelessRecipe shapeless = new BlightedShapelessRecipe(result);
        for (CraftingObject ingredient : ingredients) {
            shapeless.addIngredient(ingredient);
        }
        add(shapeless);
    }

    /**
     * Clears all registered custom recipes.
     */
    public static void clearRecipes() {
        BlightedRecipe.REGISTERED_RECIPES.clear();
    }

    /**
     * Initializes and registers all predefined recipes from categories.
     * <p>
     * Currently, this includes:
     * <ul>
     *   <li>{@link MaterialRecipes}</li>
     * </ul>
     * This method first clears all previously registered recipes.
     */
    private static final List<RecipeRegistry> REGISTRIES = List.of(
            new MaterialRecipes()
    );

    public static void initializeRecipes() {
        clearRecipes();
        REGISTRIES.forEach(RecipeRegistry::defineRecipes);
        Log.success("RecipesDirectory", "Registered " + BlightedRecipe.REGISTERED_RECIPES.size() + " custom recipes.");
    }
}
