package fr.moussax.blightedMC.smp.features.recipes;

import fr.moussax.blightedMC.smp.core.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.smp.core.items.crafting.registry.RecipeProvider;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import org.bukkit.Material;

public class EndRecipes implements RecipeProvider {
    @Override
    public void register() {

        BlightedRecipe enchantedEnderPearlRecipe = shapedRecipe("ENCHANTED_ENDER_PEARL", 2)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.ENDER_PEARL, 16)
            .build();

        BlightedRecipe enchantedEndstoneRecipe = shapedRecipe("ENCHANTED_END_STONE", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.END_STONE, 12)
            .build();

        BlightedRecipe enchantedChorusFruitRecipe = shapedRecipe("ENCHANTED_CHORUS_FRUIT", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.CHORUS_FRUIT, 12)
            .build();

        BlightedRecipe glimmeringEyeRecipe = shapedRecipe("GLIMMERING_EYE", 1)
            .shape(" i ", "iji", " i ")
            .bind('i', ItemRegistry.getItem("ENCHANTED_ENDER_PEARL"), 1)
            .bind('j', Material.OPEN_EYEBLOSSOM, 1)
            .build();

        add(
            enchantedEnderPearlRecipe,
            enchantedEndstoneRecipe,
            enchantedChorusFruitRecipe,
            glimmeringEyeRecipe
        );
    }
}
