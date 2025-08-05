package fr.moussax.blightedMC.core.items.crafting.registry;

import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.registry.recipes.MaterialRecipes;

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
public final class RecipesRegistry {
  private RecipesRegistry() {}

  /**
   * Registers a custom recipe in the game.
   *
   * @param recipe the {@link BlightedRecipe} to add
   */
  public static void add(BlightedRecipe recipe) {
    recipe.addRecipe();
  }

  /**
   * Creates and registers a shaped crafting recipe.
   * <p>
   * A shaped recipe requires a specific pattern of ingredients.
   *
   * @param result  the resulting {@link ItemManager} item
   * @param amount  the quantity of items to produce
   * @param pattern the list of {@link CraftingObject} representing the crafting grid pattern
   */
  public static void addShapedRecipe(ItemManager result, int amount, List<CraftingObject> pattern) {
    BlightedShapedRecipe shaped = new BlightedShapedRecipe(result, amount);
    shaped.setRecipe(pattern);
    add(shaped);
  }

  /**
   * Creates and registers a shapeless crafting recipe.
   * <p>
   * A shapeless recipe allows the ingredients to be placed in any order.
   *
   * @param result      the resulting {@link ItemManager} item
   * @param ingredients the list of {@link CraftingObject} ingredients
   */
  public static void addShapelessRecipe(ItemManager result, List<CraftingObject> ingredients) {
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
  public static void initializeRecipes() {
    clearRecipes();

    List<RecipeCategory> categories = List.of(
        new MaterialRecipes()
    );
    for (RecipeCategory category : categories) {
      category.registerRecipes();
    }
  }
}
