package fr.moussax.blightedMC.core.items.crafting;

import fr.moussax.blightedMC.core.items.ItemManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a shaped recipe for the custom BlightedMC crafting system.
 * The recipe is represented as a 3x3 grid (index 0–8).
 */
public final class BlightedShapedRecipe extends BlightedRecipe {

  private final ItemManager resultItem;
  private final int resultAmount;

  /**
   * Index (0–8) of the crafting grid slot whose item's attributes
   * are transferred to the resulting item. -1 if not used.
   */
  private int attributeSourceSlotIndex = -1;

  /**
   * List of crafting ingredients in slot order (0–8).
   * A null entry or CraftingObject with null manager means an empty slot.
   */
  private final List<CraftingObject> recipePattern = new ArrayList<>();

  public BlightedShapedRecipe(ItemManager resultItem, int resultAmount) {
    this.resultItem = resultItem;
    this.resultAmount = resultAmount;
  }

  @Override
  public ItemManager getResult() {
    return resultItem;
  }

  @Override
  public int getAmount() {
    return resultAmount;
  }

  /**
   * Returns an unmodifiable view of the recipe pattern.
   */
  public List<CraftingObject> getRecipe() {
    return Collections.unmodifiableList(recipePattern);
  }

  /**
   * Safely replaces the current recipe pattern.
   */
  public void setRecipe(List<CraftingObject> recipePattern) {
    this.recipePattern.clear();
    this.recipePattern.addAll(recipePattern);
  }

  /**
   * Adds a single ingredient to the recipe.
   */
  public void addIngredient(CraftingObject ingredient) {
    this.recipePattern.add(ingredient);
  }

  public int getAttributeSourceSlot() {
    return attributeSourceSlotIndex;
  }

  public void setAttributeSourceSlot(int slotIndex) {
    if (slotIndex < 0 || slotIndex >= 9) {
      throw new IllegalArgumentException("Slot index must be in range 0–8");
    }
    this.attributeSourceSlotIndex = slotIndex;
  }

  public boolean hasAttributeSourceSlot() {
    return attributeSourceSlotIndex >= 0;
  }
}
