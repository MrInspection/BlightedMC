package fr.moussax.blightedMC.engine.fishing.registry;

import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import org.bukkit.World;

/**
 * Functional handler for registering fishing loot tables for specific
 * environments and fishing methods.
 */
@FunctionalInterface
public interface FishingRegistryHandler {

    /**
     * Registers a fishing loot table for the specified environment and method.
     *
     * @param environment the world environment associated with the loot table
     * @param method the fishing method associated with the loot table
     * @param table the loot table to register
     */
    void register(
            World.Environment environment,
            FishingMethod method,
            FishingLootTable table
    );
}
