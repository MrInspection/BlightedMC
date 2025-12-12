package fr.moussax.blightedMC;

import fr.moussax.blightedMC.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.core.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.core.items.blocks.BlocksRegistry;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipesDirectory;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.game.recipes.ForgeRecipes;

public final class GlobalRegistry {
    public static void initializeAllRegistries() {
        ItemDirectory.initializeItems();
        BlocksRegistry.initializeBlocks();
        RecipesDirectory.initializeRecipes();
        ForgeRecipes.initialize();
        EntitiesRegistry.initializeEntities();
    }

    public static void clearAllRegistries() {
        ItemDirectory.clearItems();
        BlocksRegistry.clearBlocks();
        RecipesDirectory.clearRecipes();
        SpawnableEntitiesRegistry.clearEntities();
        EntitiesRegistry.clearEntities();
    }
}
