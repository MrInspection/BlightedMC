package fr.moussax.blightedMC.smp.core.items.crafting.builder;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.ShapeEncoder;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import org.bukkit.Material;

/**
 * Fluent builder for constructing {@link BlightedShapedRecipe} instances.
 * <p>
 * This builder enforces the definition of a crafting shape before ingredient
 * binding and final recipe construction.
 */
public class ShapedRecipeBuilder {

    private final BlightedItem result;
    private final int amount;
    private ShapeEncoder encoder;
    private int attributeSourceSlot = -1;

    private ShapedRecipeBuilder(BlightedItem result, int amount) {
        this.result = result;
        this.amount = amount;
    }

    /**
     * Creates a builder for a shaped recipe producing the given item.
     *
     * @param result the resulting item
     * @param amount the quantity produced
     * @return a new builder instance
     */
    public static ShapedRecipeBuilder of(BlightedItem result, int amount) {
        return new ShapedRecipeBuilder(result, amount);
    }

    /**
     * Creates a builder for a shaped recipe using a result item ID.
     *
     * @param resultId the result item identifier
     * @param amount   the quantity produced
     * @return a new builder instance
     */
    public static ShapedRecipeBuilder of(String resultId, int amount) {
        return new ShapedRecipeBuilder(ItemRegistry.getItem(resultId), amount);
    }

    /**
     * Defines the 3×3 crafting grid shape.
     * <p>
     * Must be invoked before binding ingredients.
     *
     * @param line1 first row
     * @param line2 second row
     * @param line3 third row
     * @return this builder
     */
    public ShapedRecipeBuilder shape(String line1, String line2, String line3) {
        this.encoder = new ShapeEncoder(line1, line2, line3);
        return this;
    }

    /**
     * Binds a material ingredient to a shape key.
     *
     * @param key      the shape character
     * @param material the material to bind
     * @param amount   the required amount
     * @return this builder
     * @throws IllegalStateException if the shape has not been defined
     */
    public ShapedRecipeBuilder bind(char key, Material material, int amount) {
        validateEncoder();
        encoder.bindKey(key, material, amount);
        return this;
    }

    /**
     * Binds a custom item ingredient to a shape key.
     *
     * @param key    the shape character
     * @param item   the item to bind
     * @param amount the required amount
     * @return this builder
     * @throws IllegalStateException if the shape has not been defined
     */
    public ShapedRecipeBuilder bind(char key, BlightedItem item, int amount) {
        validateEncoder();
        encoder.bindKey(key, item, amount);
        return this;
    }

    /**
     * Binds a custom item ingredient using its item ID.
     *
     * @param key    the shape character
     * @param itemId the item identifier
     * @param amount the required amount
     * @return this builder
     */
    public ShapedRecipeBuilder bind(char key, String itemId, int amount) {
        return bind(key, ItemRegistry.getItem(itemId), amount);
    }

    /**
     * Sets which slot in the 3x3 grid transfers its attributes (enchants, durability) to the result.
     * <p>
     * Slots are indexed 0-8 (Row 1: 0,1,2 | Row 2: 3,4,5 | Row 3: 6,7,8).
     *
     * @param slotIndex the index of the source slot
     * @return this builder
     */
    public ShapedRecipeBuilder attributeSource(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 9) {
            throw new IllegalArgumentException("Attribute source slot must be between 0 and 8");
        }
        this.attributeSourceSlot = slotIndex;
        return this;
    }

    /**
     * Builds the shaped recipe.
     *
     * @return the constructed {@link BlightedShapedRecipe}
     * @throws IllegalStateException if the shape has not been defined
     */
    public BlightedShapedRecipe build() {
        validateEncoder();
        BlightedShapedRecipe recipe = new BlightedShapedRecipe(result, amount);
        recipe.setRecipe(encoder.encodeCraftingRecipe());

        if (attributeSourceSlot != -1) {
            recipe.setAttributeSourceSlot(attributeSourceSlot);
        }

        return recipe;
    }

    /**
     * Ensures the recipe shape has been defined.
     */
    private void validateEncoder() {
        if (encoder == null) {
            throw new IllegalStateException("Recipe shape has not been defined yet. Call .shape() first.");
        }
    }
}
