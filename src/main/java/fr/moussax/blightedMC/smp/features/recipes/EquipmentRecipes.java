package fr.moussax.blightedMC.smp.features.recipes;

import fr.moussax.blightedMC.smp.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.registry.RecipeProvider;
import org.bukkit.Material;

public final class EquipmentRecipes implements RecipeProvider {
    @Override
    public void register() {
        BlightedRecipe rocketBoots = shapedRecipe("ROCKET_BOOTS", 1)
            .shape("aba", "cbc", "c c")
            .bind('a', Material.FIREWORK_ROCKET, 1)
            .bind('b', Material.SLIME_BALL, 1)
            .bind('c', Material.RABBIT_HIDE, 1)
            .build();

        add(rocketBoots);
    }
}
