package fr.moussax.blightedSMP.content.recipes;

import fr.moussax.blightedSMP.engine.items.recipes.crafting.BlightedRecipe;
import fr.moussax.blightedSMP.registry.RegistryModule;

import java.util.function.Consumer;

import static fr.moussax.blightedSMP.engine.items.recipes.crafting.registry.RecipeRegistry.shapedRecipe;

public class AnglerArmorRecipe implements RegistryModule<Consumer<BlightedRecipe>> {

    @Override
    public void register(Consumer<BlightedRecipe> registry) {

        BlightedRecipe anglerHelmet = shapedRecipe("ANGLER_HELMET")





    }
}
