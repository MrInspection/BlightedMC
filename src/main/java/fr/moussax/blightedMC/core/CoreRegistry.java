package fr.moussax.blightedMC.core;

import fr.moussax.blightedMC.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.core.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.core.items.registry.ItemsRegistry;
import fr.moussax.blightedMC.core.items.blocks.BlocksRegistry;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipesRegistry;

public final class CoreRegistry {

  public static void initializeAllRegistries() {
    ItemsRegistry.initializeItems();
    BlocksRegistry.initializeBlocks();
    RecipesRegistry.initializeRecipes();
    EntitiesRegistry.initializeEntities();
  }

  public static void clearAllRegistries() {
    ItemsRegistry.clearItems();
    BlocksRegistry.clearBlocks();
    RecipesRegistry.clearRecipes();
    SpawnableEntitiesRegistry.clearEntities();
    EntitiesRegistry.clearEntities();
  }
}
