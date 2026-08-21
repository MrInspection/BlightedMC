package fr.moussax.blightedMC.content.recipes;

import fr.moussax.blightedMC.engine.items.recipes.crafting.BlightedRecipe;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.items.recipes.crafting.registry.RecipeRegistryHandler;
import org.bukkit.Material;

import static fr.moussax.blightedMC.engine.items.recipes.crafting.registry.RecipeRegistry.shapedRecipe;

public final class EquipmentRecipes implements RegistryModule<RecipeRegistryHandler> {
    @Override
    public void register(RecipeRegistryHandler registry) {
        BlightedRecipe rocketBoots = shapedRecipe("ROCKET_BOOTS", 1)
                .shape("aba", "cdc", "e e")
                .bind('a', Material.PHANTOM_MEMBRANE, 1)
                .bind('b', Material.WIND_CHARGE, 1)
                .bind('c', Material.SLIME_BLOCK, 1)
                .bind('d', Material.COPPER_BOOTS, 1)
                .bind('e', Material.RABBIT_FOOT, 1)
                .attributeSource(4)
                .build();

        registry.register(rocketBoots);
    }
}
