package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.smp.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.smp.core.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.smp.core.items.blocks.BlocksRegistry;
import fr.moussax.blightedMC.smp.core.items.crafting.registry.RecipesDirectory;
import fr.moussax.blightedMC.smp.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.smp.features.recipes.ForgeRecipes;
import fr.moussax.blightedMC.smp.features.rituals.RitualsRegistry;

public final class GlobalRegistry {
    public static void initializeAllRegistries() {
        ItemDirectory.initializeItems();
        BlocksRegistry.initializeBlocks();
        RecipesDirectory.initializeRecipes();
        ForgeRecipes.initialize();
        EntitiesRegistry.initializeEntities();
        RitualsRegistry.registerRituals();
    }

    public static void clearAllRegistries() {
        ItemDirectory.clearItems();
        BlocksRegistry.clearBlocks();
        RecipesDirectory.clearRecipes();
        SpawnableEntitiesRegistry.clearEntities();
        EntitiesRegistry.clearEntities();
    }
}
