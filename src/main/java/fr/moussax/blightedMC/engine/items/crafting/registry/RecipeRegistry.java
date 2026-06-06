package fr.moussax.blightedMC.engine.items.crafting.registry;

import fr.moussax.blightedMC.content.recipes.EndRecipes;
import fr.moussax.blightedMC.content.recipes.EquipmentRecipes;
import fr.moussax.blightedMC.content.recipes.MaterialRecipes;
import fr.moussax.blightedMC.content.recipes.NetherMaterialRecipes;
import fr.moussax.blightedMC.engine.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class RecipeRegistry {

    private static final List<RecipeProvider> PROVIDERS = List.of(
        new MaterialRecipes(),
        new NetherMaterialRecipes(),
        new EndRecipes(),
        new EquipmentRecipes()
    );

    private RecipeRegistry() {
    }

    public static void initialize() {
        clear();
        PROVIDERS.forEach(RecipeProvider::register);
        Log.success("RecipesRegistry", "Registered " + BlightedRecipe.REGISTERED_RECIPES.size() + " custom recipes.");
    }

    static void registerRecipe(@NonNull BlightedRecipe recipe) {
        recipe.addRecipe();
    }

    public static void clear() {
        BlightedRecipe.REGISTERED_RECIPES.clear();
    }
}
