package fr.moussax.blightedMC.core.items.forging;

import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.crafting.CraftingObject;

import java.util.*;

/**
 * Represents a recipe for forging items in the game.
 * <p>
 * Each recipe defines a resulting item, the quantity produced, the fuel cost,
 * and the required ingredients. Recipes can be registered in the global set
 * of {@link #REGISTERED_RECIPES} to make them available for crafting.
 * </p>
 */
public record ForgeRecipe(ItemTemplate result, int amount, int fuelCost, List<CraftingObject> ingredients) {

    /** Global set of all registered forge recipes. */
    public static final Set<ForgeRecipe> REGISTERED_RECIPES = new HashSet<>();

    /**
     * Constructs a forge recipe with a list of ingredients.
     *
     * @param result      the resulting item template
     * @param amount      the number of items produced
     * @param fuelCost    the fuel cost required to forge
     * @param ingredients the ingredients required for the recipe
     */
    public ForgeRecipe(ItemTemplate result, int amount, int fuelCost, List<CraftingObject> ingredients) {
        this.result = result;
        this.amount = amount;
        this.fuelCost = fuelCost;
        this.ingredients = new ArrayList<>(ingredients);
    }

    /**
     * Constructs a forge recipe with a variable number of ingredients.
     *
     * @param result      the resulting item template
     * @param amount      the number of items produced
     * @param fuelCost    the fuel cost required to forge
     * @param ingredients the ingredients required for the recipe
     */
    public ForgeRecipe(ItemTemplate result, int amount, int fuelCost, CraftingObject... ingredients) {
        this(result, amount, fuelCost, Arrays.asList(ingredients));
    }

    /**
     * Registers this recipe in the global set of forge recipes.
     */
    public void register() {
        REGISTERED_RECIPES.add(this);
    }

    /**
     * Returns an unmodifiable list of ingredients required by this recipe.
     *
     * @return the list of ingredients
     */
    @Override
    public List<CraftingObject> ingredients() {
        return Collections.unmodifiableList(ingredients);
    }
}
