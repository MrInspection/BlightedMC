package fr.moussax.blightedMC.smp.core.items.forging.registry;

import fr.moussax.blightedMC.smp.core.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.smp.features.recipes.ForgeRecipes;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Central registry for all {@link ForgeRecipe} instances in BlightedMC.
 * <p>
 * Manages recipe registration via {@link ForgeProvider} implementations,
 * provides access to all registered forge recipes, and allows clearing
 * or reinitializing the registry during plugin startup or reload.
 */
public class ForgeRegistry {

    /** Set of all registered forge recipes. */
    public static final Set<ForgeRecipe> RECIPES = new HashSet<>();

    /** List of providers that define and register forge recipes. */
    private static final List<ForgeProvider> PROVIDERS = List.of(
        new ForgeRecipes()
    );

    /**
     * Initializes the registry by clearing existing recipes and
     * registering new ones from all providers.
     */
    public static void initialize() {
        RECIPES.clear();
        PROVIDERS.forEach(ForgeProvider::register);
        Log.success("ForgeRegistry", "Registered " + RECIPES.size() + " forge recipes.");
    }

    /**
     * Registers a single forge recipe.
     *
     * @param recipe the recipe to register
     */
    static void register(@NonNull ForgeRecipe recipe) {
        RECIPES.add(recipe);
    }

    /**
     * Returns all registered forge recipes.
     *
     * @return unmodifiable set of all recipes
     */
    public static Set<ForgeRecipe> getAll() {
        return Collections.unmodifiableSet(RECIPES);
    }

    /**
     * Clears all recipes from the registry.
     */
    public static void clear() {
        RECIPES.clear();
    }
}
