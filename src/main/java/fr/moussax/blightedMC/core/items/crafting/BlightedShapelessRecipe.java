package fr.moussax.blightedMC.core.items.crafting;

import fr.moussax.blightedMC.core.items.ItemManager;

import java.util.*;

/**
 * Represents a shapeless recipe for the custom BlightedMC crafting system.
 * Ingredients can appear in any order.
 */
public final class BlightedShapelessRecipe extends BlightedRecipe {

  private final List<CraftingObject> ingredientList = new ArrayList<>();
  private final Map<String, Integer> ingredientCountMap = new HashMap<>();
  private final ItemManager resultItem;

  public BlightedShapelessRecipe(ItemManager resultItem) {
    this.resultItem = resultItem;
  }

  @Override
  public ItemManager getResult() {
    return resultItem;
  }

  @Override
  public int getAmount() {
    return 0; // This recipe type does not define per-result amounts
  }

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

  public List<CraftingObject> getIngredients() {
    return Collections.unmodifiableList(ingredientList);
  }

  public Map<String, Integer> getIngredientCountMap() {
    return Collections.unmodifiableMap(ingredientCountMap);
  }
}
