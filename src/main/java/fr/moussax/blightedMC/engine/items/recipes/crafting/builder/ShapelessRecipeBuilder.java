package fr.moussax.blightedMC.engine.items.recipes.crafting.builder;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.engine.items.recipes.CraftingObject;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for creating {@link BlightedShapelessRecipe} instances.
 *
 * <p>Ingredients are added without regard to their position in a crafting
 * grid. One ingredient may optionally be designated as the attribute source
 * for the resulting recipe.</p>
 */
public final class ShapelessRecipeBuilder {

    private final BlightedItem result;
    private final List<CraftingObject> ingredients = new ArrayList<>();
    private CraftingObject attributeSource = null;

    private ShapelessRecipeBuilder(BlightedItem result) {
        this.result = result;
    }

    /**
     * Creates a builder for a shapeless recipe producing the specified item.
     *
     * @param result the item produced by the recipe
     * @return a new shapeless recipe builder
     */
    public static ShapelessRecipeBuilder of(BlightedItem result) {
        return new ShapelessRecipeBuilder(result);
    }

    /**
     * Creates a builder for a shapeless recipe producing the item registered
     * under the specified ID.
     *
     * @param resultId the ID of the result item
     * @return a new shapeless recipe builder
     * @throws IllegalArgumentException if the item ID is not registered
     */
    public static ShapelessRecipeBuilder of(String resultId) {
        return new ShapelessRecipeBuilder(ItemRegistry.getItem(resultId));
    }

    /**
     * Adds a material ingredient to the recipe.
     *
     * @param material the material required by the recipe
     * @param amount   the required amount
     * @return this builder
     */
    public ShapelessRecipeBuilder addIngredient(Material material, int amount) {
        return addIngredient(material, amount, false);
    }

    /**
     * Adds a material ingredient to the recipe and optionally designates it
     * as the attribute source.
     *
     * @param material          the material required by the recipe
     * @param amount            the required amount
     * @param isAttributeSource whether this ingredient provides the attributes
     *                           for the resulting item
     * @return this builder
     */
    public ShapelessRecipeBuilder addIngredient(
            Material material,
            int amount,
            boolean isAttributeSource
    ) {
        CraftingObject obj = new CraftingObject(material, amount);
        ingredients.add(obj);
        if (isAttributeSource) this.attributeSource = obj;
        return this;
    }

    /**
     * Adds a custom item ingredient to the recipe.
     *
     * @param item   the custom item required by the recipe
     * @param amount the required amount
     * @return this builder
     */
    public ShapelessRecipeBuilder addIngredient(BlightedItem item, int amount) {
        return addIngredient(item, amount, false);
    }

    /**
     * Adds a custom item ingredient to the recipe and optionally designates
     * it as the attribute source.
     *
     * @param item              the custom item required by the recipe
     * @param amount            the required amount
     * @param isAttributeSource whether this ingredient provides the attributes
     *                           for the resulting item
     * @return this builder
     */
    public ShapelessRecipeBuilder addIngredient(
            BlightedItem item,
            int amount,
            boolean isAttributeSource
    ) {
        CraftingObject obj = new CraftingObject(item, amount);
        ingredients.add(obj);
        if (isAttributeSource) this.attributeSource = obj;
        return this;
    }

    /**
     * Adds a registered custom item as an ingredient.
     *
     * @param itemId the ID of the required custom item
     * @param amount the required amount
     * @return this builder
     * @throws IllegalArgumentException if the item ID is not registered
     */
    public ShapelessRecipeBuilder addIngredient(String itemId, int amount) {
        return addIngredient(ItemRegistry.getItem(itemId), amount, false);
    }

    /**
     * Adds a registered custom item as an ingredient and optionally
     * designates it as the attribute source.
     *
     * @param itemId            the ID of the required custom item
     * @param amount            the required amount
     * @param isAttributeSource whether this ingredient provides the attributes
     *                           for the resulting item
     * @return this builder
     * @throws IllegalArgumentException if the item ID is not registered
     */
    public ShapelessRecipeBuilder addIngredient(
            String itemId,
            int amount,
            boolean isAttributeSource
    ) {
        return addIngredient(ItemRegistry.getItem(itemId), amount, isAttributeSource);
    }

    /**
     * Builds the configured shapeless recipe.
     *
     * <p>All configured ingredients and the optional attribute source are
     * transferred to the resulting recipe.</p>
     *
     * @return the constructed shapeless recipe
     */
    public BlightedShapelessRecipe build() {
        BlightedShapelessRecipe recipe = new BlightedShapelessRecipe(result);
        for (CraftingObject ingredient : ingredients) {
            recipe.addIngredient(ingredient);
        }

        if (attributeSource != null) {
            recipe.setAttributeSource(attributeSource);
        }

        return recipe;
    }
}
