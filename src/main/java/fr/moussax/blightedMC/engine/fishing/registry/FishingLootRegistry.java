package fr.moussax.blightedMC.engine.fishing.registry;

import fr.moussax.blightedMC.content.fishing.EndFishing;
import fr.moussax.blightedMC.content.fishing.NetherFishing;
import fr.moussax.blightedMC.content.fishing.OverworldFishing;
import fr.moussax.blightedMC.content.fishing.OverworldLavaFishing;
import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FishingLootRegistry {

    private static final Map<World.Environment, Map<FishingMethod, FishingLootProvider>> REGISTRY = new EnumMap<>(World.Environment.class);
    private static final Map<String, FishingLootTable> TABLE_CACHE = new HashMap<>();
    private static final FishingLootTable EMPTY_TABLE = FishingLootTable.builder().build();

    private static final List<FishingLootProvider> PROVIDERS = List.of(
        new NetherFishing(),
        new OverworldLavaFishing(),
        new OverworldFishing(),
        new EndFishing()
    );

    private FishingLootRegistry() {
    }

    public static void initialize() {
        clear();
        PROVIDERS.forEach(FishingLootProvider::register);
        Log.success("FishingLootRegistry", "Registered " + countRegistrations() + " fishing loot tables.");
    }

    static void register(World.Environment environment, FishingMethod method, FishingLootProvider provider) {
        REGISTRY.computeIfAbsent(environment, k -> new EnumMap<>(FishingMethod.class)).put(method, provider);
    }

    public static FishingLootTable getTable(World.Environment environment, FishingMethod method) {
        Map<FishingMethod, FishingLootProvider> envMap = REGISTRY.get(environment);
        if (envMap == null) return EMPTY_TABLE;

        FishingLootProvider provider = envMap.get(method);
        if (provider == null) return EMPTY_TABLE;

        String cacheKey = environment.name() + ":" + method.name();
        return TABLE_CACHE.computeIfAbsent(cacheKey, k -> provider.provide());
    }

    public static void clear() {
        REGISTRY.clear();
        TABLE_CACHE.clear();
    }

    private static int countRegistrations() {
        return REGISTRY.values().stream()
            .mapToInt(Map::size)
            .sum();
    }
}
