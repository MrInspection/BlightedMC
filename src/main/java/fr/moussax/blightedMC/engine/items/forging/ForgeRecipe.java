package fr.moussax.blightedMC.engine.items.forging;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.crafting.CraftingObject;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a forging recipe for producing a Blighted item.
 *
 * <p>A recipe defines the forged item, its output amount, required ingredients,
 * and fuel cost.</p>
 */
public final class ForgeRecipe {

    @Getter
    private final BlightedItem forgedItem;
    @Getter
    private final int forgedAmount;
    private final List<CraftingObject> ingredients;
    @Getter
    private final int fuelCost;

    private ForgeRecipe(
            BlightedItem forgedItem,
            int amount,
            List<CraftingObject> ingredients,
            int fuelCost
    ) {
        this.forgedItem = forgedItem;
        this.forgedAmount = amount;
        this.ingredients = ingredients;
        this.fuelCost = fuelCost;
    }

    /**
     * Returns the ingredients required by this recipe.
     *
     * @return an unmodifiable list of required ingredients
     */
    public List<CraftingObject> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * Builder for creating {@link ForgeRecipe} instances.
     */
    public static final class Builder {
        private final BlightedItem forgedItem;
        private final int forgedAmount;
        private final List<CraftingObject> ingredients = new ArrayList<>();
        private int fuelCost = 0;

        private Builder(BlightedItem forgedItem, int forgedAmount) {
            this.forgedItem = forgedItem;
            this.forgedAmount = forgedAmount;
        }

        /**
         * Creates a recipe builder for a Blighted item.
         *
         * @param item   the item produced by the recipe
         * @param amount the amount produced by the recipe
         * @return a new recipe builder
         */
        public static Builder of(BlightedItem item, int amount) {
            return new Builder(item, amount);
        }

        /**
         * Creates a recipe builder for a registered Blighted item.
         *
         * @param itemId the identifier of the item produced by the recipe
         * @param amount the amount produced by the recipe
         * @return a new recipe builder
         */
        public static Builder of(String itemId, int amount) {
            return new Builder(ItemRegistry.getItem(itemId), amount);
        }

        /**
         * Adds an ingredient to the recipe.
         *
         * @param ingredient the ingredient to add
         * @return this builder
         */
        public Builder addIngredient(CraftingObject ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        /**
         * Adds a material ingredient to the recipe.
         *
         * @param material the required material
         * @param amount   the required amount
         * @return this builder
         */
        public Builder addIngredient(Material material, int amount) {
            this.ingredients.add(new CraftingObject(material, amount));
            return this;
        }

        /**
         * Adds a Blighted item ingredient to the recipe.
         *
         * @param item   the required Blighted item
         * @param amount the required amount
         * @return this builder
         */
        public Builder addIngredient(BlightedItem item, int amount) {
            this.ingredients.add(new CraftingObject(item, amount));
            return this;
        }

        /**
         * Adds a registered Blighted item as an ingredient.
         *
         * @param itemId the identifier of the required item
         * @param amount the required amount
         * @return this builder
         */
        public Builder addIngredient(String itemId, int amount) {
            this.ingredients.add(new CraftingObject(ItemRegistry.getItem(itemId), amount));
            return this;
        }

        /**
         * Adds multiple ingredients to the recipe.
         *
         * @param ingredients the ingredients to add
         * @return this builder
         */
        public Builder ingredients(CraftingObject... ingredients) {
            this.ingredients.addAll(Arrays.asList(ingredients));
            return this;
        }

        /**
         * Sets the fuel cost required by the recipe.
         *
         * @param fuelCost the required fuel amount
         * @return this builder
         */
        public Builder fuelCost(int fuelCost) {
            this.fuelCost = fuelCost;
            return this;
        }

        /**
         * Builds the configured forging recipe.
         *
         * @return the configured forge recipe
         */
        public ForgeRecipe build() {
            return new ForgeRecipe(forgedItem, forgedAmount, ingredients, fuelCost);
        }
    }
}
