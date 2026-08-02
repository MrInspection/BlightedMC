package fr.moussax.blightedMC.engine.fishing.registry;

import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import org.bukkit.World;

/**
 * Custom functional interface for registering fishing loot tables.
 */
@FunctionalInterface
public interface FishingRegistryHandler {
    void register(World.Environment environment, FishingMethod method, FishingLootTable table);
}
