package fr.moussax.blightedMC.engine.items.recipes.crafting.builder;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.engine.items.recipes.ShapeEncoder;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import org.bukkit.Material;

/**
 * Fluent builder for creating {@link BlightedShapedRecipe} instances.
 *
 * <p>Defines a three-row crafting shape and binds each shape character to a
 * material or custom item. An optional crafting slot may be designated as the
 * attribute source for the resulting item.</p>
 */
public final class ShapedRecipeBuilder {

    private final BlightedItem result;
    private final int amount;
    private ShapeEncoder encoder;
    private int attributeSourceSlot = -1;

    private ShapedRecipeBuilder(BlightedItem result, int amount) {
        this.result = result;
        this.amount = amount;
    }

    /**
     * Creates a builder for a shaped recipe producing the specified item.
     *
     * @param result the item produced by the recipe
     * @param amount the amount produced by the recipe
     * @return a new shaped recipe builder
     */
    public static ShapedRecipeBuilder of(BlightedItem result, int amount) {
        return new ShapedRecipeBuilder(result, amount);
    }

    /**
     * Creates a builder for a shaped recipe producing the item registered
     * under the specified ID.
     *
     * @param resultId the ID of the result item
     * @param amount   the amount produced by the recipe
     * @return a new shaped recipe builder
     * @throws IllegalArgumentException if the item ID is not registered
     */
    public static ShapedRecipeBuilder of(String resultId, int amount) {
        return new ShapedRecipeBuilder(ItemRegistry.getItem(resultId), amount);
    }

    /**
     * Defines the three-row crafting shape used by the recipe.
     *
     * @param line1 the first row of the crafting shape
     * @param line2 the second row of the crafting shape
     * @param line3 the third row of the crafting shape
     * @return this builder
     */
    public ShapedRecipeBuilder shape(String line1, String line2, String line3) {
        this.encoder = new ShapeEncoder(line1, line2, line3);
        return this;
    }

    /**
     * Binds a crafting shape character to a material.
     *
     * @param key      the character used in the recipe shape
     * @param material the material represented by the character
     * @param amount   the required amount of the material
     * @return this builder
     * @throws IllegalStateException if {@link #shape(String, String, String)}
     *                               has not been called
     */
    public ShapedRecipeBuilder bind(char key, Material material, int amount) {
        validateEncoder();
        encoder.bindKey(key, material, amount);
        return this;
    }

    /**
     * Binds a crafting shape character to a custom item.
     *
     * @param key    the character used in the recipe shape
     * @param item   the custom item represented by the character
     * @param amount the required amount of the item
     * @return this builder
     * @throws IllegalStateException if {@link #shape(String, String, String)}
     *                               has not been called
     */
    public ShapedRecipeBuilder bind(char key, BlightedItem item, int amount) {
        validateEncoder();
        encoder.bindKey(key, item, amount);
        return this;
    }

    /**
     * Binds a crafting shape character to a registered custom item.
     *
     * @param key    the character used in the recipe shape
     * @param itemId the ID of the custom item represented by the character
     * @param amount the required amount of the item
     * @return this builder
     * @throws IllegalArgumentException if the item ID is not registered
     * @throws IllegalStateException    if {@link #shape(String, String, String)}
     *                                  has not been called
     */
    public ShapedRecipeBuilder bind(char key, String itemId, int amount) {
        return bind(key, ItemRegistry.getItem(itemId), amount);
    }

    /**
     * Designates a crafting grid slot as the attribute source.
     *
     * <p>The slot index uses the standard zero-based nine-slot crafting grid,
     * ranging from {@code 0} to {@code 8}.</p>
     *
     * @param slotIndex the zero-based crafting grid slot
     * @return this builder
     * @throws IllegalArgumentException if the slot index is outside the range
     *                                  {@code 0} to {@code 8}
     */
    public ShapedRecipeBuilder attributeSource(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 9) {
            throw new IllegalArgumentException("Attribute source slot must be between 0 and 8");
        }
        this.attributeSourceSlot = slotIndex;
        return this;
    }

    /**
     * Builds the configured shaped recipe.
     *
     * <p>The recipe shape is encoded and transferred to the resulting recipe,
     * along with the optional attribute source slot.</p>
     *
     * @return the constructed shaped recipe
     * @throws IllegalStateException if the recipe shape has not been defined
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

    private void validateEncoder() {
        if (encoder == null) {
            throw new IllegalStateException("Recipe shape has not been defined yet. Call .shape() first.");
        }
    }
}
