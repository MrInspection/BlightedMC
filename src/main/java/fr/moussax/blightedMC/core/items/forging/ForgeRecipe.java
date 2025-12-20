package fr.moussax.blightedMC.core.items.forging;

import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;

import java.util.*;

/**
 * Defines a forge recipe describing how an item is produced via forging.
 *
 * <p>A recipe specifies the resulting item template, output amount,
 * required ingredients, and fuel cost. All instances are automatically
 * registered in {@link #REGISTRY} upon creation.</p>
 */
public final class ForgeRecipe {

    /**
     * Global set of all registered forge recipes.
     */
    public static final Set<ForgeRecipe> REGISTRY = new HashSet<>();

    private final ItemTemplate forgedItem;
    private final int forgedAmount;
    private final List<CraftingObject> ingredients;
    private final int fuelCost;

    private ForgeRecipe(
        ItemTemplate forgedItem,
        int amount,
        List<CraftingObject> ingredients,
        int fuelCost
    ) {
        this.forgedItem = forgedItem;
        this.forgedAmount = amount;
        this.ingredients = ingredients;
        this.fuelCost = fuelCost;

        REGISTRY.add(this);
    }

    /**
     * @return the item produced by this recipe
     */
    public ItemTemplate getForgedItem() {
        return forgedItem;
    }

    /**
     * @return the number of items produced by this recipe
     */
    public int getForgedAmount() {
        return forgedAmount;
    }

    /**
     * @return an unmodifiable list of required ingredients
     */
    public List<CraftingObject> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * @return the fuel cost required to forge this recipe
     */
    public int getFuelCost() {
        return fuelCost;
    }

    /**
     * Builder for creating and registering {@link ForgeRecipe} instances.
     */
    public static final class Builder {
        private ItemTemplate forgedItem;
        private int forgedAmount;
        private final List<CraftingObject> ingredients = new ArrayList<>();
        private int fuelCost;

        /**
         * Sets the item produced by the recipe.
         *
         * @param forgedItem the resulting item template
         * @return this builder instance
         */
        public Builder forgedItem(ItemTemplate forgedItem) {
            this.forgedItem = forgedItem;
            return this;
        }

        /**
         * Sets the number of items produced by the recipe.
         *
         * @param amount number of items produced
         * @return this builder instance
         */
        public Builder forgedAmount(int amount) {
            this.forgedAmount = amount;
            return this;
        }

        /**
         * Adds multiple ingredients required by the recipe.
         *
         * @param ingredients crafting ingredients
         * @return this builder instance
         */
        public Builder ingredients(CraftingObject... ingredients) {
            this.ingredients.addAll(Arrays.asList(ingredients));
            return this;
        }

        /**
         * Adds a single ingredient required by the recipe.
         *
         * @param ingredient crafting ingredient
         * @return this builder instance
         */
        public Builder addIngredient(CraftingObject ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        /**
         * Sets the fuel cost required to forge the recipe.
         *
         * @param fuelCost required fuel amount
         * @return this builder instance
         */
        public Builder fuelCost(int fuelCost) {
            this.fuelCost = fuelCost;
            return this;
        }

        /**
         * Builds and registers the forge recipe.
         *
         * @return the created {@link ForgeRecipe}
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
