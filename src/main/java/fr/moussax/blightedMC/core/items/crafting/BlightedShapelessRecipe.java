package fr.moussax.blightedMC.core.items.crafting;

import fr.moussax.blightedMC.core.items.ItemTemplate;

import java.util.*;

/**
 * Represents a shapeless recipe in the custom BlightedMC crafting system.
 * <p>
 * Unlike shaped recipes, the order of the ingredients in a shapeless recipe
 * does not matter. A recipe is considered valid if all required ingredients
 * are present in the correct quantities.
 */
public final class BlightedShapelessRecipe extends BlightedRecipe {
  private final List<CraftingObject> ingredientList = new ArrayList<>();
  private final Map<String, Integer> ingredientCountMap = new HashMap<>();
  private final ItemTemplate resultItemTemplate;

  /**
   * Creates a new shapeless recipe with the given result item.
   *
   * @param resultItemTemplate the item produced by this recipe
   */
  public BlightedShapelessRecipe(ItemTemplate resultItemTemplate) {
    this.resultItemTemplate = resultItemTemplate;
  }

  /**
   * Returns the result of this recipe.
   *
   * @return the {@link ItemTemplate} produced by the recipe
   */
  @Override
  public ItemTemplate getResult() {
    return resultItemTemplate;
  }

  /**
   * Returns the quantity produced per craft.
   * <p>
   * Shapeless recipes in this implementation do not define variable amounts
   * and always return 0 by default.
   *
   * @return the amount produced, always 0
   */
  @Override
  public int getAmount() {
    return 0; // This recipe type does not define per-result amounts
  }

  /**
   * Adds an ingredient to this shapeless recipe.
   *
   * @param ingredient the ingredient to add
   * @throws IllegalArgumentException if the ingredient is neither custom nor vanilla
   */
  public void addIngredient(CraftingObject ingredient) {
    ingredientList.add(ingredient);
    String itemId;
    if (ingredient.isCustom()) {
      itemId = ingredient.getManager().getItemId();
    } else if (ingredient.isVanilla()) {
      itemId = "vanilla:" + ingredient.getVanillaItem().getType().name().toLowerCase();
    } else {
      throw new IllegalArgumentException("Ingredient must be custom or vanilla");
    }
    ingredientCountMap.merge(itemId, ingredient.getAmount(), Integer::sum);
  }

  /**
   * Returns an unmodifiable list of all ingredients in this recipe.
   *
   * @return a list of recipe ingredients
   */
  public List<CraftingObject> getIngredients() {
    return Collections.unmodifiableList(ingredientList);
  }

  /**
   * Returns an unmodifiable map of ingredient identifiers to their total required counts.
   *
   * @return a map of ingredient identifiers to quantities
   */
  public Map<String, Integer> getIngredientCountMap() {
    return Collections.unmodifiableMap(ingredientCountMap);
  }
}
