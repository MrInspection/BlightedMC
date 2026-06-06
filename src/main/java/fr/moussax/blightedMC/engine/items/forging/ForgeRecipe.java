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

    public List<CraftingObject> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public static final class Builder {
        private final BlightedItem forgedItem;
        private final int forgedAmount;
        private final List<CraftingObject> ingredients = new ArrayList<>();
        private int fuelCost = 0;

        private Builder(BlightedItem forgedItem, int forgedAmount) {
            this.forgedItem = forgedItem;
            this.forgedAmount = forgedAmount;
        }

        public static Builder of(BlightedItem item, int amount) {
            return new Builder(item, amount);
        }

        public static Builder of(String itemId, int amount) {
            return new Builder(ItemRegistry.getItem(itemId), amount);
        }

        public Builder addIngredient(CraftingObject ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        public Builder addIngredient(Material material, int amount) {
            this.ingredients.add(new CraftingObject(material, amount));
            return this;
        }

        public Builder addIngredient(BlightedItem item, int amount) {
            this.ingredients.add(new CraftingObject(item, amount));
            return this;
        }

        public Builder addIngredient(String itemId, int amount) {
            this.ingredients.add(new CraftingObject(ItemRegistry.getItem(itemId), amount));
            return this;
        }

        public Builder ingredients(CraftingObject... ingredients) {
            this.ingredients.addAll(Arrays.asList(ingredients));
            return this;
        }

        public Builder fuelCost(int fuelCost) {
            this.fuelCost = fuelCost;
            return this;
        }

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
