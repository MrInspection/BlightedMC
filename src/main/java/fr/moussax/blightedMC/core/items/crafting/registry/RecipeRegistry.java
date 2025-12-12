package fr.moussax.blightedMC.core.items.crafting.registry;

/**
 * Represents a category of custom crafting recipes.
 * <p>
 * Implementations of this interface are responsible for defining and
 * registering a set of recipes that belong to the same category,
 * such as material upgrades, weapon crafting, or utility items.
 */
@FunctionalInterface
public interface RecipeRegistry {
    /**
     * Registers all recipes that belong to this category.
     * <p>
     * Implementations should define all the custom recipes
     * relevant to the category and register them using
     * {@link RecipesDirectory}.
     */
    void defineRecipes();
}
