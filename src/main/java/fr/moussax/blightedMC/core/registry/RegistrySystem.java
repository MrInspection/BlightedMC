package fr.moussax.blightedMC.core.registry;

import fr.moussax.blightedMC.core.entities.EntitiesRegistry;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.items.blocks.BlocksRegistry;
import fr.moussax.blightedMC.core.items.crafting.registry.RecipesRegistry;

/**
 * Centralized manager for all plugin registries.
 * <p>
 * This class provides unified methods to initialize and clear
 * the registries responsible for managing:
 * <ul>
 *   <li>Item definitions and their variants</li>
 *   <li>Custom block registrations</li>
 *   <li>Crafting recipes</li>
 *   <li>Custom and spawnable entity registrations</li>
 * </ul>
 * <p>
 * It ensures proper load order and lifecycle handling of these registries,
 * enabling consistent plugin behavior and resource management.
 * <p>
 * Usage of this class guarantees that all game content managed by the plugin
 * is properly registered or released when necessary.
 */
public final class RegistrySystem {

  /**
   * Initializes all plugin registries in the appropriate sequence.
   * <p>
   * This method must be called during plugin startup or when reloading
   * to ensure that all items, blocks, recipes, and entities are
   * fully registered and ready for use.
   */
  public static void initializeAllRegistries() {
    ItemsRegistry.initializeItems();
    BlocksRegistry.initializeBlocks();
    RecipesRegistry.initializeRecipes();
    SpawnableEntitiesRegistry.initializeEntities();
    EntitiesRegistry.initializeEntities();
  }

  /**
   * Clears all data from the plugin registries.
   * <p>
   * This method releases references held by items, blocks, recipes,
   * and entities registries to facilitate garbage collection and
   * prevent memory leaks during plugin shutdown or reload.
   */
  public static void clearAllRegistries() {
    ItemsRegistry.clearItems();
    BlocksRegistry.clearBlocks();
    RecipesRegistry.clearRecipes();
    SpawnableEntitiesRegistry.clearEntities();
    EntitiesRegistry.clearEntities();
  }
}
