package fr.moussax.blightedMC.core.registry;

import fr.moussax.blightedMC.core.entities.EntitiesRegistry;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.items.blocks.BlocksRegistry;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipesRegistry;

/**
 * Manages initialization and clearing of all plugin registries.
 */
public final class RegistrySystem {

  public static void initializeAllRegistries() {
    ItemsRegistry.initializeItems();
    BlocksRegistry.initializeBlocks();
    RecipesRegistry.initializeRecipes();
    SpawnableEntitiesRegistry.initializeEntities();
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
