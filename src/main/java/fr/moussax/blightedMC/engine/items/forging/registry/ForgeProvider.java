package fr.moussax.blightedMC.engine.items.forging.registry;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.forging.ForgeRecipe;

import java.util.List;

@FunctionalInterface
public interface ForgeProvider {

    void register();

    default void add(ForgeRecipe recipe) {
        ForgeRegistry.register(recipe);
    }

    default void add(ForgeRecipe... recipes) {
        for (ForgeRecipe recipe : recipes) {
            ForgeRegistry.register(recipe);
        }
    }

    default void add(List<ForgeRecipe> recipes) {
        recipes.forEach(ForgeRegistry::register);
    }

    default ForgeRecipe.Builder forgeRecipe(BlightedItem result, int amount) {
        return ForgeRecipe.Builder.of(result, amount);
    }

    default ForgeRecipe.Builder forgeRecipe(String resultId, int amount) {
        return ForgeRecipe.Builder.of(resultId, amount);
    }
}
