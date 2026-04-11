package fr.moussax.blightedMC.engine.items.crafting.builder;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.crafting.BlightedShapedRecipe;
import fr.moussax.blightedMC.engine.items.crafting.ShapeEncoder;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import org.bukkit.Material;

public final class ShapedRecipeBuilder {

    private final BlightedItem result;
    private final int amount;
    private ShapeEncoder encoder;
    private int attributeSourceSlot = -1;

    private ShapedRecipeBuilder(BlightedItem result, int amount) {
        this.result = result;
        this.amount = amount;
    }

    public static ShapedRecipeBuilder of(BlightedItem result, int amount) {
        return new ShapedRecipeBuilder(result, amount);
    }

    public static ShapedRecipeBuilder of(String resultId, int amount) {
        return new ShapedRecipeBuilder(ItemRegistry.getItem(resultId), amount);
    }

    public ShapedRecipeBuilder shape(String line1, String line2, String line3) {
        this.encoder = new ShapeEncoder(line1, line2, line3);
        return this;
    }

    public ShapedRecipeBuilder bind(char key, Material material, int amount) {
        validateEncoder();
        encoder.bindKey(key, material, amount);
        return this;
    }

    public ShapedRecipeBuilder bind(char key, BlightedItem item, int amount) {
        validateEncoder();
        encoder.bindKey(key, item, amount);
        return this;
    }

    public ShapedRecipeBuilder bind(char key, String itemId, int amount) {
        return bind(key, ItemRegistry.getItem(itemId), amount);
    }

    public ShapedRecipeBuilder attributeSource(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 9) {
            throw new IllegalArgumentException("Attribute source slot must be between 0 and 8");
        }
        this.attributeSourceSlot = slotIndex;
        return this;
    }

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
