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

public final class ForgeRegistry {

    public static final Set<ForgeRecipe> RECIPES = new HashSet<>();

    private static final List<RegistryModule<ForgeRegistryHandler>> PROVIDERS = List.of(
            new ForgeRecipes()
    );

    private ForgeRegistry() {
    }

    public static void initialize() {
        clear();
        PROVIDERS.forEach(module -> module.register(ForgeRegistry::register));
        Log.success("ForgeRegistry", "Registered " + RECIPES.size() + " forge recipes.");
    }

    public static void register(@NonNull ForgeRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static Set<ForgeRecipe> getAll() {
        return Collections.unmodifiableSet(RECIPES);
    }

    public static void clear() {
        RECIPES.clear();
    }
}
