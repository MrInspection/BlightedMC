package fr.moussax.blightedMC.engine.items.recipes.forging.registry;

import fr.moussax.blightedMC.engine.items.recipes.forging.ForgeRecipe;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.utils.debug.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Registry for {@link ForgeRecipe} definitions available to the forge system.
 *
 * <p>Recipes are provided by registered {@link RegistryModule} implementations
 * and loaded when {@link #initialize(List)} is called.</p>
 */
public final class ForgeRegistry {

    /**
     * The set of forge recipes currently registered.
     */
    public static final Set<ForgeRecipe> RECIPES = new HashSet<>();

    private ForgeRegistry() {
    }

    /**
     * Initializes the forge registry using the provided list of modules.
     *
     * <p>Clears existing registrations before invoking configured forge modules.</p>
     *
     * @param modules the list of forge modules to load
     */
    public static void initialize(List<RegistryModule<Consumer<ForgeRecipe>>> modules) {
        clear();
        modules.forEach(module -> module.register(ForgeRegistry::register));
        Log.success("ForgeRegistry", "Registered " + RECIPES.size() + " forge recipes.");
    }

    /**
     * Registers a forge recipe.
     *
     * @param recipe the recipe to add to the registry
     */
    public static void register(ForgeRecipe recipe) {
        RECIPES.add(recipe);
    }

    /**
     * Retrieves an unmodifiable set of all registered forge recipes.
     *
     * @return an unmodifiable set of registered forge recipes
     */
    public static Set<ForgeRecipe> getAll() {
        return Collections.unmodifiableSet(RECIPES);
    }

    /**
     * Clears all registered forge recipes.
     */
    public static void clear() {
        RECIPES.clear();
    }
}
