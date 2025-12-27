package fr.moussax.blightedMC.smp.core.items.forging;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a forging recipe in BlightedMC.
 * <p>
 * A forge recipe defines the output item, quantity, required ingredients,
 * and fuel cost. Instances are intended to be created via
 * {@link Builder}, which provides a fluent interface for recipe construction.
 */
public final class ForgeRecipe {

    private final BlightedItem forgedItem;
    private final int forgedAmount;
    private final List<CraftingObject> ingredients;
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
     * Returns the item produced by this forge recipe.
     *
     * @return the resulting BlightedItem
     */
    public BlightedItem getForgedItem() {
        return forgedItem;
    }

    /**
     * Returns the quantity of items produced.
     *
     * @return number of items produced
     */
    public int getForgedAmount() {
        return forgedAmount;
    }

    /**
     * Returns the required ingredients to forge this item.
     *
     * @return unmodifiable list of ingredients
     */
    public List<CraftingObject> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * Returns the fuel cost required to perform this forging operation.
     *
     * @return fuel cost
     */
    public int getFuelCost() {
        return fuelCost;
    }

    /**
     * Builder for creating {@link ForgeRecipe} instances.
     * <p>
     * Provides a fluent interface to define the output item, amount,
     * ingredients, and fuel cost. Use {@link #build()} to finalize.
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
         * Creates a new builder for a forge recipe producing the given item.
         *
         * @param item   the resulting item
         * @param amount the quantity produced
         * @return new builder instance
         */
        public static Builder of(BlightedItem item, int amount) {
            return new Builder(item, amount);
        }

        /**
         * Creates a new builder using the result item's ID.
         *
         * @param itemId the result item identifier
         * @param amount the quantity produced
         * @return new builder instance
         */
        public static Builder of(String itemId, int amount) {
            return new Builder(ItemRegistry.getItem(itemId), amount);
        }

        /**
         * Adds a custom ingredient to the recipe.
         *
         * @param ingredient the ingredient to add
         * @return this builder
         */
        public Builder addIngredient(CraftingObject ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        /**
         * Adds a vanilla material as an ingredient.
         *
         * @param material the material to add
         * @param amount   required quantity
         * @return this builder
         */
        public Builder addIngredient(Material material, int amount) {
            this.ingredients.add(new CraftingObject(material, amount));
            return this;
        }

        /**
         * Adds a custom item as an ingredient.
         *
         * @param item   the custom item
         * @param amount required quantity
         * @return this builder
         */
        public Builder addIngredient(BlightedItem item, int amount) {
            this.ingredients.add(new CraftingObject(item, amount));
            return this;
        }

        /**
         * Adds a custom item by ID as an ingredient.
         *
         * @param itemId the custom item ID
         * @param amount required quantity
         * @return this builder
         */
        public Builder addIngredient(String itemId, int amount) {
            this.ingredients.add(new CraftingObject(ItemRegistry.getItem(itemId), amount));
            return this;
        }

        /**
         * Adds multiple ingredients at once.
         *
         * @param ingredients array of ingredients to add
         * @return this builder
         */
        public Builder ingredients(CraftingObject... ingredients) {
            this.ingredients.addAll(Arrays.asList(ingredients));
            return this;
        }

        /**
         * Sets the fuel cost for this recipe.
         *
         * @param fuelCost the fuel amount required
         * @return this builder
         */
        public Builder fuelCost(int fuelCost) {
            this.fuelCost = fuelCost;
            return this;
        }

        /**
         * Builds the {@link ForgeRecipe} instance.
         *
         * @return the constructed forge recipe
         */
        public ForgeRecipe build() {
            return new ForgeRecipe(
                forgedItem,
                forgedAmount,
                ingredients,
                fuelCost
            );
        }
    }
}
