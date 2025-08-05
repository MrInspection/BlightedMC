package fr.moussax.blightedMC.core.items.crafting;

import fr.moussax.blightedMC.core.items.ItemManager;

import java.util.*;

/**
 * Represents a shaped recipe for the custom BlightedMC crafting system.
 * <p>
 * Shaped recipes are defined as a fixed 3×3 crafting grid (slots 0–8).
 * The placement of ingredients matters, unlike shapeless recipes.
 */
public final class BlightedShapedRecipe extends BlightedRecipe {
  private final ItemManager resultItem;
  private final int resultAmount;

  /**
   * Index (0–8) of the crafting grid slot whose item's attributes
   * are transferred to the resulting item.
   * <p>-1 indicates that no slot is used for attribute transfer.
   */
  private int attributeSourceSlotIndex = -1;

  /**
   * Ordered list of crafting ingredients in slot order (0–8).
   * <p>A null entry or a {@link CraftingObject} with a null manager
   * represents an empty slot.
   */
  private final List<CraftingObject> recipePattern = new ArrayList<>();

  /**
   * Creates a new-shaped recipe with a given result item and output amount.
   *
   * @param resultItem   the resulting custom item
   * @param resultAmount the number of items produced
   */
  public BlightedShapedRecipe(ItemManager resultItem, int resultAmount) {
    this.resultItem = resultItem;
    this.resultAmount = resultAmount;
  }

  /**
   * Returns the resulting custom item of this recipe.
   *
   * @return the result item
   */
  @Override
  public ItemManager getResult() {
    return resultItem;
  }

  /**
   * Returns the number of items produced by this recipe.
   *
   * @return the result amount
   */
  @Override
  public int getAmount() {
    return resultAmount;
  }

  /**
   * Returns an unmodifiable view of the recipe pattern.
   * <p>The list is ordered according to the 3×3 crafting grid slots (0–8).
   *
   * @return the recipe pattern
   */
  public List<CraftingObject> getRecipe() {
    return Collections.unmodifiableList(recipePattern);
  }

  /**
   * Replaces the current recipe pattern with a new one.
   *
   * @param recipePattern the new pattern (size up to 9)
   */
  public void setRecipe(List<CraftingObject> recipePattern) {
    this.recipePattern.clear();
    this.recipePattern.addAll(recipePattern);
  }

  /**
   * Adds a single ingredient to the recipe.
   * <p>Ingredients are added in slot order (0–8).
   *
   * @param ingredient the ingredient to add
   */
  public void addIngredient(CraftingObject ingredient) {
    this.recipePattern.add(ingredient);
  }

  /**
   * Returns the crafting grid slot index used for attribute transfer.
   *
   * @return the attribute source slot index, or -1 if not used
   */
  public int getAttributeSourceSlot() {
    return attributeSourceSlotIndex;
  }

  /**
   * Sets the slot index for attribute transfer.
   *
   * @param slotIndex the slot index (0–8)
   * @throws IllegalArgumentException if the index is outside 0–8
   */
  public void setAttributeSourceSlot(int slotIndex) {
    if (slotIndex < 0 || slotIndex >= 9) {
      throw new IllegalArgumentException("Slot index must be in range 0–8");
    }
    this.attributeSourceSlotIndex = slotIndex;
  }

  /**
   * Checks if this recipe transfers attributes from a crafting slot.
   *
   * @return true if a source slot is defined, false otherwise
   */
  public boolean hasAttributeSourceSlot() {
    return attributeSourceSlotIndex >= 0;
  }

  /**
   * Returns a map of all ingredients and their total required amounts.
   * <p>Keys are item identifiers, which can be either:
   * <ul>
   *   <li>Custom items: {@code manager.getItemId()}</li>
   *   <li>Vanilla items: {@code "vanilla:material_name"}</li>
   * </ul>
   *
   * @return an unmodifiable map of ingredient identifiers to quantities
   * @throws IllegalArgumentException if an ingredient is neither custom nor vanilla
   */
  public Map<String, Integer> getIngredientCountMap() {
    var ingredientCountMap = new HashMap<String, Integer>();
    for (CraftingObject ingredient : recipePattern) {
      if (ingredient == null) continue;
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
    return Collections.unmodifiableMap(ingredientCountMap);
  }
}
