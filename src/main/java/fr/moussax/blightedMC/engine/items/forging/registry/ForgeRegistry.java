package fr.moussax.blightedMC.engine.items.forging.registry;

import fr.moussax.blightedMC.content.recipes.ForgeRecipes;
import fr.moussax.blightedMC.engine.items.forging.ForgeRecipe;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Registry for {@link ForgeRecipe} definitions available to the forge system.
 *
 * <p>Recipes are provided by registered {@link RegistryModule} implementations
 * and loaded when {@link #initialize()} is called.</p>
 */
public final class ForgeRegistry {

    /**
     * The set of forge recipes currently registered.
     */
    public static final Set<ForgeRecipe> RECIPES = new HashSet<>();

    private static final List<RegistryModule<ForgeRegistryHandler>> PROVIDERS = List.of(
            new ForgeRecipes()
    );

    private ForgeRegistry() {
    }

    /**
     * Initializes the forge recipe registry.
     *
     * <p>Previously registered recipes are cleared before all configured
     * recipe providers are loaded.</p>
     */
    public static void initialize() {
        clear();
        PROVIDERS.forEach(module -> module.register(ForgeRegistry::register));
        Log.success("ForgeRegistry", "Registered " + RECIPES.size() + " forge recipes.");
    }

    /**
     * Registers a forge recipe.
     *
     * @param recipe the recipe to register
     */
    public static void register(@NonNull ForgeRecipe recipe) {
        RECIPES.add(recipe);
    }

    /**
     * Returns all currently registered forge recipes.
     *
     * @return an unmodifiable view of the registered recipes
     */
    public static Set<ForgeRecipe> getAll() {
        return Collections.unmodifiableSet(RECIPES);
    }

    /**
     * Removes all recipes currently registered in the forge registry.
     */
    public static void clear() {
        RECIPES.clear();
    }
}
