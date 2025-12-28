package fr.moussax.blightedMC.smp.core.fishing.registry;

import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.FishingMethod;
import org.bukkit.World;

public interface FishingLootProvider {

    /**
     * Registers this provider's loot tables to the registry.
     * Implementations should call {@code add} methods here.
     */
    void register();

    /**
     * Provides the loot table for this provider.
     *
     * @return the fishing loot table
     */
    FishingLootTable provide();

    /**
     * Registers this provider for a specific environment and fishing method.
     *
     * @param environment the world environment
     * @param method      the fishing method (WATER or LAVA)
     */
    default void add(World.Environment environment, FishingMethod method) {
        FishingLootRegistry.register(environment, method, this);
    }

    /**
     * Registers this provider for water fishing in the specified environment.
     *
     * @param environment the world environment
     */
    default void addWater(World.Environment environment) {
        add(environment, FishingMethod.WATER);
    }

    /**
     * Registers this provider for lava fishing in the specified environment.
     *
     * @param environment the world environment
     */
    default void addLava(World.Environment environment) {
        add(environment, FishingMethod.LAVA);
    }
}