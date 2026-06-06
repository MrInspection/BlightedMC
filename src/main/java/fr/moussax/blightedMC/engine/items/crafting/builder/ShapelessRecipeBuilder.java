package fr.moussax.blightedMC.engine.items.crafting.builder;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.crafting.BlightedShapelessRecipe;
import fr.moussax.blightedMC.engine.items.crafting.CraftingObject;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class ShapelessRecipeBuilder {

    private final BlightedItem result;
    private final List<CraftingObject> ingredients = new ArrayList<>();
    private CraftingObject attributeSource = null;

    private ShapelessRecipeBuilder(BlightedItem result) {
        this.result = result;
    }

    public static ShapelessRecipeBuilder of(BlightedItem result) {
        return new ShapelessRecipeBuilder(result);
    }

    public static ShapelessRecipeBuilder of(String resultId) {
        return new ShapelessRecipeBuilder(ItemRegistry.getItem(resultId));
    }

    public ShapelessRecipeBuilder addIngredient(Material material, int amount) {
        return addIngredient(material, amount, false);
    }

    public ShapelessRecipeBuilder addIngredient(Material material, int amount, boolean isAttributeSource) {
        CraftingObject obj = new CraftingObject(material, amount);
        ingredients.add(obj);
        if (isAttributeSource) this.attributeSource = obj;
        return this;
    }

    public ShapelessRecipeBuilder addIngredient(BlightedItem item, int amount) {
        return addIngredient(item, amount, false);
    }

    public ShapelessRecipeBuilder addIngredient(BlightedItem item, int amount, boolean isAttributeSource) {
        CraftingObject obj = new CraftingObject(item, amount);
        ingredients.add(obj);
        if (isAttributeSource) this.attributeSource = obj;
        return this;
    }

    public ShapelessRecipeBuilder addIngredient(String itemId, int amount) {
        return addIngredient(ItemRegistry.getItem(itemId), amount, false);
    }

    public ShapelessRecipeBuilder addIngredient(String itemId, int amount, boolean isAttributeSource) {
        return addIngredient(ItemRegistry.getItem(itemId), amount, isAttributeSource);
    }

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
