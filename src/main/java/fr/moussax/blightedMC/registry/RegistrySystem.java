package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.engine.entities.rituals.registry.RitualRegistry;
import fr.moussax.blightedMC.engine.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.engine.items.blocks.registry.BlockRegistry;
import fr.moussax.blightedMC.engine.items.recipes.crafting.registry.RecipeRegistry;
import fr.moussax.blightedMC.engine.items.recipes.forging.registry.ForgeRegistry;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;

/**
 * Coordinates the initialization and cleanup of all BlightedMC registries.
 *
 * <p>The registry system provides a single entry point for loading and clearing
 * custom items, blocks, recipes, forge recipes, entities, rituals, and fishing
 * loot tables.</p>
 */
public final class RegistrySystem {

    /**
     * Initializes all BlightedMC registries in dependency order.
     *
     * <p>Registries are initialized from their respective content providers
     * before dependent systems are loaded.</p>
     */
    public static void initialize() {
        ItemRegistry.initialize();
        BlockRegistry.initialize();
        RecipeRegistry.initialize();
        ForgeRegistry.initialize();
        EntitiesRegistry.initialize();
        RitualRegistry.initialize();
        FishingLootRegistry.initialize();
    }

    /**
     * Clears all registered BlightedMC content.
     *
     * <p>This removes the contents of every managed registry without
     * reinitializing them.</p>
     */
    public static void clear() {
        ItemRegistry.clear();
        BlockRegistry.clear();
        RecipeRegistry.clear();
        ForgeRegistry.clear();
        EntitiesRegistry.clear();
        RitualRegistry.clear();
        FishingLootRegistry.clear();
    }
}
