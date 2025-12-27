package fr.moussax.blightedMC.smp.core.items.crafting.builder;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for constructing {@link BlightedShapelessRecipe} instances.
 * <p>
 * Allows incremental declaration of ingredients without positional
 * constraints.
 */
public class ShapelessRecipeBuilder {

    private final BlightedItem result;
    private final List<CraftingObject> ingredients = new ArrayList<>();

    private ShapelessRecipeBuilder(BlightedItem result) {
        this.result = result;
    }

    /**
     * Creates a builder for a shapeless recipe producing the given item.
     *
     * @param result the resulting item
     * @return a new builder instance
     */
    public static ShapelessRecipeBuilder of(BlightedItem result) {
        return new ShapelessRecipeBuilder(result);
    }

    /**
     * Creates a builder for a shapeless recipe using a result item ID.
     *
     * @param resultId the result item identifier
     * @return a new builder instance
     */
    public static ShapelessRecipeBuilder of(String resultId) {
        return new ShapelessRecipeBuilder(ItemRegistry.getItem(resultId));
    }

    /**
     * Adds a material ingredient.
     *
     * @param material the material to add
     * @param amount   the required amount
     * @return this builder
     */
    public ShapelessRecipeBuilder addIngredient(Material material, int amount) {
        ingredients.add(new CraftingObject(material, amount));
        return this;
    }

    /**
     * Adds a custom item ingredient.
     *
     * @param item   the item to add
     * @param amount the required amount
     * @return this builder
     */
    public ShapelessRecipeBuilder addIngredient(BlightedItem item, int amount) {
        ingredients.add(new CraftingObject(item, amount));
        return this;
    }

    /**
     * Adds a custom item ingredient using its item ID.
     *
     * @param itemId the item identifier
     * @param amount the required amount
     * @return this builder
     */
    public ShapelessRecipeBuilder addIngredient(String itemId, int amount) {
        return addIngredient(ItemRegistry.getItem(itemId), amount);
    }

    /**
     * Builds the shapeless recipe.
     *
     * @return the constructed {@link BlightedShapelessRecipe}
     */
    public BlightedShapelessRecipe build() {
        BlightedShapelessRecipe recipe = new BlightedShapelessRecipe(result);
        for (CraftingObject ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }
        return recipe;
    }
}
