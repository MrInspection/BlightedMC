package fr.moussax.blightedMC.content.recipes;

import fr.moussax.blightedMC.engine.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistryHandler;
import org.bukkit.Material;

import static fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistry.shapedRecipe;

public final class EquipmentRecipes implements RegistryModule<RecipeRegistryHandler> {
    @Override
    public void register(RecipeRegistryHandler registry) {
        BlightedRecipe rocketBoots = shapedRecipe("ROCKET_BOOTS", 1)
                .shape("aba", "cbc", "c c")
                .bind('a', Material.FIREWORK_ROCKET, 1)
                .bind('b', Material.SLIME_BALL, 1)
                .bind('c', Material.RABBIT_HIDE, 1)
                .build();

        registry.register(rocketBoots);
    }
}
