package fr.moussax.blightedMC.core.items.crafting.registry;

import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.core.items.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.core.items.crafting.registry.categories.MaterialRecipes;

import java.util.List;

public final class RecipesRegistry {
  private RecipesRegistry() {}

  public static void add(BlightedRecipe recipe) {
    recipe.addRecipe();
  }

  public static void addShapedRecipe(ItemManager result, int amount, List<CraftingObject> pattern) {
    BlightedShapedRecipe shaped = new BlightedShapedRecipe(result, amount);
    shaped.setRecipe(pattern);
    add(shaped);
  }

  public static void addShapelessRecipe(ItemManager result, List<CraftingObject> ingredients) {
    BlightedShapelessRecipe shapeless = new BlightedShapelessRecipe(result);
    for (CraftingObject ingredient : ingredients) {
      shapeless.addIngredient(ingredient);
    }
    add(shapeless);
  }

  public static void clearRecipes() {
    BlightedRecipe.REGISTERED_RECIPES.clear();
  }

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
