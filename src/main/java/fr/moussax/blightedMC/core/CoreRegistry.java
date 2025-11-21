package fr.moussax.blightedMC.core;

import fr.moussax.blightedMC.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.core.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.core.items.blocks.BlocksRegistry;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipesRegistry;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;

public final class CoreRegistry {
    public static void initializeAllRegistries() {
        ItemDirectory.initializeItems();
        BlocksRegistry.initializeBlocks();
        RecipesRegistry.initializeRecipes();
        EntitiesRegistry.initializeEntities();
    }

    public static void clearAllRegistries() {
        ItemDirectory.clearItems();
        BlocksRegistry.clearBlocks();
        RecipesRegistry.clearRecipes();
        SpawnableEntitiesRegistry.clearEntities();
        EntitiesRegistry.clearEntities();
    }
}
