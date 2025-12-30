package fr.moussax.blightedMC.smp.core.items.crafting;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Concrete implementation of a shapeless Blighted crafting recipe.
 * <p>
 * Ingredient order is ignored, and attribute transfer may occur from
 * a designated ingredient instance found in the crafting grid.
 */
public final class BlightedShapelessRecipe extends BlightedRecipe {
    private final List<CraftingObject> ingredientList = new ArrayList<>();
    private final Map<String, Integer> ingredientCountMap = new HashMap<>();
    private final BlightedItem resultBlightedItem;

    /**
     * Ingredient acting as the attribute source.
     * {@code null} disables attribute transfer.
     */
    private CraftingObject attributeSourceIngredient = null;

    public BlightedShapelessRecipe(BlightedItem resultBlightedItem) {
        this.resultBlightedItem = resultBlightedItem;
    }

    @Override
    public BlightedItem getResult() {
        return resultBlightedItem;
    }

    @Override
    public int getAmount() {
        return 0;
    }

    /**
     * Builds the result item and transfers attributes from the first
     * matching source ingredient found in the grid, if configured.
     *
     * @param craftingGrid crafting grid contents
     * @return assembled result item
     */
    @Override
    public ItemStack assemble(List<ItemStack> craftingGrid) {
        ItemStack result = resultBlightedItem.toItemStack().clone();

        if (attributeSourceIngredient != null) {
            String targetId = attributeSourceIngredient.getId();
            for (ItemStack stack : craftingGrid) {
                if (stack == null || stack.getType() == Material.AIR) continue;

                String stackId = Utilities.resolveItemId(stack, targetId);
                if (stackId.equals(targetId)) {
                    transferAttributes(stack, result);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Registers an ingredient and updates its required count.
     *
     * @param ingredient recipe ingredient
     */
    public void addIngredient(CraftingObject ingredient) {
        ingredientList.add(ingredient);
        String itemId;
        if (ingredient.isCustom()) {
            itemId = ingredient.getManager().getItemId();
        } else if (ingredient.isVanilla()) {
            itemId = Utilities.resolveItemId(ingredient.getVanillaItem(), "vanilla:");
        } else {
            throw new IllegalArgumentException("Ingredient must be custom or vanilla");
        }
        ingredientCountMap.merge(itemId, ingredient.getAmount(), Integer::sum);
    }

    /**
     * Defines which ingredient provides enchantments and durability.
     *
     * @param ingredient registered ingredient
     */
    public void setAttributeSource(CraftingObject ingredient) {
        if (!ingredientList.contains(ingredient)) {
            throw new IllegalArgumentException("Attribute source must be a registered ingredient of this recipe");
        }
        this.attributeSourceIngredient = ingredient;
    }

    /** @return immutable list of registered ingredients */
    public List<CraftingObject> getIngredients() {
        return Collections.unmodifiableList(ingredientList);
    }

    /** @return immutable map of item ID → required amount */
    public Map<String, Integer> getIngredientCountMap() {
        return Collections.unmodifiableMap(ingredientCountMap);
    }
}
