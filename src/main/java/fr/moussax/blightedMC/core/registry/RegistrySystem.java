package fr.moussax.blightedMC.core.registry;

import fr.moussax.blightedMC.core.entities.EntitiesRegistry;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.items.blocks.BlocksRegistry;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipesRegistry;

/**
 * Central registry orchestrator.
 * <p>
 * Handles the initialization and clearing of all registries related to:
 * <ul>
 *   <li>Items and blocks</li>
 *   <li>Crafting recipes</li>
 *   <li>Custom entities and spawnable entities</li>
 * </ul>
 *
 * <p>Acts as a single entry point to manage registry lifecycle.
 */
public final class RegistrySystem {

  /**
   * Initializes all registries in the correct order.
   * <p>
   * Required to make items, blocks, recipes, and entities available
   * to the rest of the plugin.
   */
  public static void initializeAllRegistries() {
    ItemsRegistry.initializeItems();
    BlocksRegistry.initializeBlocks();
    RecipesRegistry.initializeRecipes();
    SpawnableEntitiesRegistry.initializeEntities();
    EntitiesRegistry.initializeEntities();
  }

  /**
   * Clears all registry data.
   * <p>
   * Releases all items, blocks, recipes, and entity registrations
   * to free memory and prevent stale references.
   */
  public static void clearAllRegistries() {
    ItemsRegistry.clearItems();
    BlocksRegistry.clearBlocks();
    RecipesRegistry.clearRecipes();
    SpawnableEntitiesRegistry.clearEntities();
    EntitiesRegistry.clearEntities();
  }
}
