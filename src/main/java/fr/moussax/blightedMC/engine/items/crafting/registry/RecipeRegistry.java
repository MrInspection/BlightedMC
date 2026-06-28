package fr.moussax.blightedMC.engine.items.crafting.registry;

import fr.moussax.blightedMC.content.recipes.EndRecipes;
import fr.moussax.blightedMC.content.recipes.EquipmentRecipes;
import fr.moussax.blightedMC.content.recipes.MaterialRecipes;
import fr.moussax.blightedMC.content.recipes.NetherMaterialRecipes;
import fr.moussax.blightedMC.engine.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.engine.items.crafting.builder.ShapedRecipeBuilder;
import fr.moussax.blightedMC.engine.items.crafting.builder.ShapelessRecipeBuilder;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.utils.debug.Log;
import org.jspecify.annotations.NonNull;

import java.util.List;

import java.util.function.Consumer;

public final class RecipeRegistry {

    private static final List<RegistryModule<RecipeRegistryHandler>> PROVIDERS = List.of(
        new MaterialRecipes(),
        new NetherMaterialRecipes(),
        new EndRecipes(),
        new EquipmentRecipes()
    );

    private RecipeRegistry() {
    }

    public static void initialize() {
        clear();
        PROVIDERS.forEach(module -> module.register(RecipeRegistry::register));
        Log.success("RecipesRegistry", "Registered " + BlightedRecipe.REGISTERED_RECIPES.size() + " custom recipes.");
    }

    public static void register(@NonNull BlightedRecipe recipe) {
        recipe.addRecipe();
    }

    public static void register(BlightedRecipe... recipes) {
        for (BlightedRecipe recipe : recipes) {
            register(recipe);
        }
    }

    public static ShapedRecipeBuilder shapedRecipe(String resultId, int amount) {
        return ShapedRecipeBuilder.of(resultId, amount);
    }

    public static ShapelessRecipeBuilder shapelessRecipe(String resultId, int amount) {
        return ShapelessRecipeBuilder.of(resultId);
    }

    public static void clear() {
        BlightedRecipe.REGISTERED_RECIPES.clear();
    }
}
