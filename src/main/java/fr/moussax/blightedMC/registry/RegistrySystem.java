package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.smp.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.smp.core.entities.rituals.registry.RitualRegistry;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.smp.core.items.blocks.registry.BlockRegistry;
import fr.moussax.blightedMC.smp.core.items.crafting.registry.RecipeRegistry;
import fr.moussax.blightedMC.smp.core.items.forging.registry.ForgeRegistry;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;

/**
 * Centralized manager for initializing and clearing all game registries.
 * <p>
 * Provides static methods to initialize or clear items, blocks, crafting recipes,
 * forging recipes, entities, and rituals in a single call. This ensures consistent
 * setup and teardown across the plugin.
 */
public final class RegistrySystem {

    /** Initializes all registries in the proper order. */
    public static void initialize() {
        ItemRegistry.initialize();
        BlockRegistry.initialize();
        RecipeRegistry.initialize();
        ForgeRegistry.initialize();
        EntitiesRegistry.initialize();
        RitualRegistry.initialize();
        FishingLootRegistry.initialize();
    }

    /** Clears all registries, removing all registered data. */
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

