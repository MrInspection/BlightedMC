package fr.moussax.blightedMC.smp.core.items.crafting;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Abstract base class for all BlightedMC crafting recipes.
 * <p>
 * Provides shared functionality for recipe registration, result retrieval,
 * and matching against a 3×3 crafting grid. Subclasses implement specific
 * behavior for shaped or shapeless recipes, handling either fixed patterns
 * or order-independent ingredient matching.
 */
public sealed abstract class BlightedRecipe
    permits BlightedShapedRecipe, BlightedShapelessRecipe {

    /** Global registry of all registered crafting recipes. */
    public static final Set<BlightedRecipe> REGISTERED_RECIPES = new HashSet<>();

    /**
     * Returns the resulting custom item of this recipe.
     *
     * @return the recipe's result
     */
    public abstract BlightedItem getResult();

    /**
     * Returns the number of items produced by this recipe.
     *
     * @return quantity produced
     */
    public abstract int getAmount();

    /**
     * Registers this recipe in the global recipe registry.
     */
    public void addRecipe() {
        REGISTERED_RECIPES.add(this);
    }

    /**
     * Finds all recipes matching the given 3×3 crafting grid.
     * <p>
     * The grid should be provided as a list of nine {@link ItemStack} elements
     * in row-major order. Empty or air-only grids return an empty set.
     *
     * @param craftingGrid the 3×3 crafting grid
     * @return set of recipes that match the grid
     */
    public static Set<BlightedRecipe> findMatchingRecipes(List<ItemStack> craftingGrid) {
        boolean isEmpty = craftingGrid.stream()
            .allMatch(item -> item == null || item.getType() == Material.AIR);
        if (isEmpty) return Collections.emptySet();

        List<String> craftingGridItemIds = resolveItemIdsFromGrid(craftingGrid);
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

    /** Extracts custom or vanilla item IDs from the crafting grid. */
    private static List<String> resolveItemIdsFromGrid(List<ItemStack> craftingGrid) {
        List<String> itemIdsInGrid = new ArrayList<>(craftingGrid.size());

        for (ItemStack stack : craftingGrid) {
            if (stack == null || stack.getType() == Material.AIR) {
                itemIdsInGrid.add("");
                continue;
            }
            itemIdsInGrid.add(Utilities.resolveItemId(stack, ""));
        }
        return itemIdsInGrid;
    }

    /** Checks if a shaped recipe matches the given crafting grid. */
    private static boolean matchesShapedRecipe(BlightedShapedRecipe recipe,
                                               List<ItemStack> craftingGrid,
                                               List<String> craftingGridItemIds) {

        List<CraftingObject> expectedPattern = recipe.getRecipe();
        if (expectedPattern.size() != craftingGrid.size()) return false;

        for (int slotIndex = 0; slotIndex < expectedPattern.size(); slotIndex++) {
            CraftingObject expectedSlot = expectedPattern.get(slotIndex);
            String currentItemId = craftingGridItemIds.get(slotIndex);

            if (expectedSlot == null || (expectedSlot.getManager() == null && !expectedSlot.isVanilla())) {
                if (!currentItemId.isEmpty()) return false;
                continue;
            }

            String expectedItemId;
            if (expectedSlot.isCustom()) {
                expectedItemId = expectedSlot.getManager().getItemId();
            } else if (expectedSlot.isVanilla()) {
                expectedItemId = "vanilla:" + expectedSlot.getVanillaItem().getType().name();
            } else {
                return false;
            }

            if (!currentItemId.equals(expectedItemId)) return false;

            ItemStack currentStack = craftingGrid.get(slotIndex);
            if (currentStack == null || currentStack.getAmount() < expectedSlot.getAmount()) return false;
        }

        return true;
    }

    /**
     * Checks if a shapeless recipe matches the given crafting grid.
     */
    private static boolean matchesShapelessRecipe(BlightedShapelessRecipe recipe, List<ItemStack> craftingGrid, List<String> craftingGridItemIds) {
        Map<String, Integer> remainingRequiredCounts = new HashMap<>(recipe.getIngredientCountMap());

        for (int slotIndex = 0; slotIndex < craftingGrid.size(); slotIndex++) {
            ItemStack currentStack = craftingGrid.get(slotIndex);
            if (currentStack == null || currentStack.getType() == Material.AIR) continue;

            String currentItemId = craftingGridItemIds.get(slotIndex);

            if (currentItemId.isEmpty() || !remainingRequiredCounts.containsKey(currentItemId)) {
                return false;
            }

            int newRemaining = remainingRequiredCounts.get(currentItemId) - currentStack.getAmount();
            if (newRemaining < 0) return false;
            remainingRequiredCounts.put(currentItemId, newRemaining);
        }

        return remainingRequiredCounts.values().stream().allMatch(amount -> amount == 0);
    }
}
