package fr.moussax.blightedMC.engine.fishing.registry;

import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import org.bukkit.World;

public interface FishingLootProvider {

    void register();

    FishingLootTable provide();

    default void add(World.Environment environment, FishingMethod method) {
        FishingLootRegistry.register(environment, method, this);
    }

    default void addWater(World.Environment environment) {
        add(environment, FishingMethod.WATER);
    }

    default void addLava(World.Environment environment) {
        add(environment, FishingMethod.LAVA);
    }
}
