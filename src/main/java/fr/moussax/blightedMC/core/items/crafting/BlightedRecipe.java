package fr.moussax.blightedMC.core.items.crafting;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Base class for all BlightedMC Crafting System (shaped & shapeless recipes).
 */
public sealed abstract class BlightedRecipe
    permits BlightedShapedRecipe, BlightedShapelessRecipe {

  public static final Set<BlightedRecipe> REGISTERED_RECIPES = new HashSet<>();
  public abstract ItemManager getResult();

  public abstract int getAmount();

  public void addRecipe() {
    REGISTERED_RECIPES.add(this);
  }

  /**
   * Checks the crafting grid for matching registered recipes.
   *
   * @param craftingGrid Crafting a grid as a 3×3 list of ItemStacks
   * @return Set of all matching Blighted recipes
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
   * Extracts the persistent custom item IDs from a crafting grid.
   * For vanilla items, assigns ID "vanilla:{itemname_lowercase}".
   * Empty slots return an empty string.
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
   * Checks if a shaped recipe matches the given crafting grid.
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
