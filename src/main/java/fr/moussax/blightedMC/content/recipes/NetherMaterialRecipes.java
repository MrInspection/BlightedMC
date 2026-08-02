package fr.moussax.blightedMC.content.recipes;

import fr.moussax.blightedMC.engine.items.crafting.BlightedRecipe;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistryHandler;
import fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistry;
import static fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistry.shapedRecipe;
import static fr.moussax.blightedMC.engine.items.crafting.registry.RecipeRegistry.shapelessRecipe;
import org.bukkit.Material;

public class NetherMaterialRecipes implements RegistryModule<RecipeRegistryHandler> {
    @Override
    public void register(RecipeRegistryHandler registry) {

        BlightedRecipe enchantedGhastTearRecipe = shapedRecipe("ENCHANTED_GHAST_TEAR", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.GHAST_TEAR, 1)
            .build();

        BlightedRecipe enchantedMagmaCreamRecipe = shapedRecipe("ENCHANTED_MAGMA_CREAM", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.MAGMA_CREAM, 8)
            .build();

        BlightedRecipe enchantedQuartzRecipe = shapedRecipe("ENCHANTED_QUARTZ", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.QUARTZ, 8)
            .build();

        BlightedRecipe enchantedBlazePowderRecipe = shapedRecipe("ENCHANTED_BLAZE_POWDER", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.BLAZE_POWDER, 8)
            .build();

        BlightedRecipe enchantedBlazeRodRecipe = shapedRecipe("ENCHANTED_BLAZE_ROD", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', "ENCHANTED_BLAZE_POWDER", 8)
            .build();

        BlightedRecipe enchantedGlowstoneRecipe = shapedRecipe("ENCHANTED_GLOWSTONE_DUST", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.GLOWSTONE_DUST, 8)
            .build();

        BlightedRecipe enchantedNetherWartRecipe = shapedRecipe("ENCHANTED_NETHER_WART", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.NETHER_WART, 12)
            .build();

        BlightedRecipe enchantedSulfurRecipe = shapedRecipe("ENCHANTED_SULFUR", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', "SULFUR", 8)
            .build();

        BlightedRecipe enchantedNetherrackRecipe = shapedRecipe("ENCHANTED_NETHERRACK", 1)
            .shape(" i ", "iii", " i ")
            .bind('i', Material.NETHERRACK, 12)
            .build();

                registry.register(enchantedGhastTearRecipe);
        registry.register(enchantedMagmaCreamRecipe);
        registry.register(enchantedQuartzRecipe);
        registry.register(enchantedBlazePowderRecipe);
        registry.register(enchantedBlazeRodRecipe);
        registry.register(enchantedGlowstoneRecipe);
        registry.register(enchantedNetherWartRecipe);
        registry.register(enchantedSulfurRecipe);
    }
}
