package fr.moussax.blightedMC.core.items.crafting;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Base class for all recipes in the BlightedMC crafting system.
 * <p>
 * Supports both shaped and shapeless recipes. Subclasses handle
 * specific pattern matching logic.
 * <ul>
 *   <li>{@link BlightedShapedRecipe} – fixed 3×3 pattern</li>
 *   <li>{@link BlightedShapelessRecipe} – ingredients in any order</li>
 * </ul>
 */
public sealed abstract class BlightedRecipe
    permits BlightedShapedRecipe, BlightedShapelessRecipe {

  public static final Set<BlightedRecipe> REGISTERED_RECIPES = new HashSet<>();
  public abstract ItemTemplate getResult();

  /**
   * Returns the number of items produced by this recipe.
   *
   * @return the result quantity
   */
  public abstract int getAmount();

  /**
   * Registers this recipe into the global crafting system.
   */
  public void addRecipe() {
    REGISTERED_RECIPES.add(this);
  }

  /**
   * Searches for all recipes that match the provided 3×3 crafting grid.
   *
   * @param craftingGrid the 3×3 crafting grid as a list of ItemStacks
   * @return all matching recipes, or an empty set if none match
   */
  public static Set<BlightedRecipe> findMatchingRecipes(List<ItemStack> craftingGrid) {
    boolean isEmpty = craftingGrid.stream()
        .allMatch(item -> item == null || item.getType() == Material.AIR);
    if (isEmpty) return Collections.emptySet();

    List<String> craftingGridItemIds = extractItemIds(craftingGrid);
    Set<BlightedRecipe> matchingRecipes = new HashSet<>();

    for (BlightedRecipe recipe : REGISTERED_RECIPES) {
      if (recipe.getResult() == null) continue;

      boolean isMatch = switch (recipe) {
        case BlightedShapedRecipe shapedRecipe ->
            matchesShapedRecipe(shapedRecipe, craftingGrid, craftingGridItemIds);
        case BlightedShapelessRecipe shapelessRecipe ->
            matchesShapelessRecipe(shapelessRecipe, craftingGrid, craftingGridItemIds);
      };

      if (isMatch) matchingRecipes.add(recipe);
    }

    return matchingRecipes;
  }

  /**
   * Extracts item identifiers for each slot in the crafting grid.
   * <ul>
   *   <li>Custom items → PersistentData key {@code id}</li>
   *   <li>Vanilla items → {@code vanilla:{material_lowercase}}</li>
   *   <li>Empty slots → empty string</li>
   * </ul>
   *
   * @param craftingGrid the crafting grid
   * @return list of item IDs per slot
   */
  private static List<String> extractItemIds(List<ItemStack> craftingGrid) {
    List<String> itemIdsInGrid = new ArrayList<>(craftingGrid.size());

    for (ItemStack stack : craftingGrid) {
      if (stack == null || stack.getType() == Material.AIR) {
        itemIdsInGrid.add("");
        continue;
      }

      String customItemId = Objects.requireNonNull(stack.getItemMeta())
          .getPersistentDataContainer()
          .get(new NamespacedKey(BlightedMC.getInstance(), "id"), PersistentDataType.STRING);

      if (customItemId != null) {
        itemIdsInGrid.add(customItemId);
      } else {
        // Vanilla item ID format
        itemIdsInGrid.add("vanilla:" + stack.getType().name().toLowerCase(Locale.ROOT));
      }
    }

    return itemIdsInGrid;
  }

  /**
   * Checks if a shaped recipe exactly matches a given crafting grid.
   * <p>Validates:
   * <ul>
   *   <li>Slot-by-slot item identity (custom or vanilla)</li>
   *   <li>Required amounts per slot</li>
   *   <li>Empty slots must remain empty</li>
   * </ul>
   *
   * @param recipe the shaped recipe to test
   * @param craftingGrid the crafting grid
   * @param craftingGridItemIds precomputed slot item IDs
   * @return true if the grid matches the shaped recipe
   */
  private static boolean matchesShapedRecipe(BlightedShapedRecipe recipe,
                                             List<ItemStack> craftingGrid,
                                             List<String> craftingGridItemIds) {

    List<CraftingObject> expectedPattern = recipe.getRecipe();
    if (expectedPattern.size() != craftingGrid.size()) return false;

    for (int slotIndex = 0; slotIndex < expectedPattern.size(); slotIndex++) {
      CraftingObject expectedSlot = expectedPattern.get(slotIndex);
      String currentItemId = craftingGridItemIds.get(slotIndex);

      // Slot expected to be empty must be empty
      if (expectedSlot == null || (expectedSlot.getManager() == null && !expectedSlot.isVanilla())) {
        if (!currentItemId.isEmpty()) return false;
        continue;
      }

      String expectedItemId;
      if (expectedSlot.isCustom()) {
        expectedItemId = expectedSlot.getManager().getItemId();
      } else if (expectedSlot.isVanilla()) {
        expectedItemId = "vanilla:" + expectedSlot.getVanillaItem().getType().name().toLowerCase(Locale.ROOT);
      } else {
        return false;
      }

      // Must match expected item ID
      if (!currentItemId.equals(expectedItemId)) return false;

      // Must meet the required amount
      ItemStack currentStack = craftingGrid.get(slotIndex);
      if (currentStack == null || currentStack.getAmount() < expectedSlot.getAmount()) return false;
    }

    return true;
  }

  /**
   * Checks if a shapeless recipe matches the given crafting grid.
   * <p>Validation is order-independent but quantity-dependent.
   *
   * @param recipe the shapeless recipe to test
   * @param craftingGrid the crafting grid
   * @param craftingGridItemIds precomputed slot item IDs
   * @return true if the grid matches the shapeless recipe
   */
  private static boolean matchesShapelessRecipe(BlightedShapelessRecipe recipe,
                                                List<ItemStack> craftingGrid,
                                                List<String> craftingGridItemIds) {

    Map<String, Integer> remainingRequiredCounts = new HashMap<>(recipe.getIngredientCountMap());

    for (int slotIndex = 0; slotIndex < craftingGrid.size(); slotIndex++) {
      ItemStack currentStack = craftingGrid.get(slotIndex);
      if (currentStack == null || currentStack.getType() == Material.AIR) continue;

      String currentItemId = craftingGridItemIds.get(slotIndex);
      if (currentItemId.isEmpty() || !remainingRequiredCounts.containsKey(currentItemId)) return false;

      int newRemaining = remainingRequiredCounts.get(currentItemId) - currentStack.getAmount();
      if (newRemaining < 0) return false; // Too many items
      remainingRequiredCounts.put(currentItemId, newRemaining);
    }

    // All ingredients must be exactly used
    return remainingRequiredCounts.values().stream().allMatch(amount -> amount == 0);
  }
}
