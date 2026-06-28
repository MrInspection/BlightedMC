package fr.moussax.blightedMC.engine.fishing.registry;

import fr.moussax.blightedMC.content.fishing.EndFishing;
import fr.moussax.blightedMC.content.fishing.NetherFishing;
import fr.moussax.blightedMC.content.fishing.OverworldFishing;
import fr.moussax.blightedMC.content.fishing.OverworldLavaFishing;
import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class FishingLootRegistry {

    private static final Map<World.Environment, Map<FishingMethod, FishingLootTable>> REGISTRY = new EnumMap<>(World.Environment.class);
    private static final FishingLootTable EMPTY_TABLE = FishingLootTable.builder().build();

    private static final List<RegistryModule<FishingRegistryHandler>> PROVIDERS = List.of(
            new NetherFishing(),
            new OverworldLavaFishing(),
            new OverworldFishing(),
            new EndFishing()
    );

    private FishingLootRegistry() {
    }

    public static void initialize() {
        clear();
        PROVIDERS.forEach(module -> module.register(FishingLootRegistry::register));
        Log.success("FishingLootRegistry", "Registered " + countRegistrations() + " fishing loot tables.");
    }

    public static void register(World.Environment environment, FishingMethod method, FishingLootTable table) {
        REGISTRY.computeIfAbsent(environment, k -> new EnumMap<>(FishingMethod.class)).put(method, table);
    }

    public static FishingLootTable getTable(World.Environment environment, FishingMethod method) {
        Map<FishingMethod, FishingLootTable> envMap = REGISTRY.get(environment);
        if (envMap == null) return EMPTY_TABLE;

        FishingLootTable table = envMap.get(method);
        return table != null ? table : EMPTY_TABLE;
    }

    public static void clear() {
        REGISTRY.clear();
    }

    private static int countRegistrations() {
        return REGISTRY.values().stream()
                .mapToInt(Map::size)
                .sum();
    }
}
