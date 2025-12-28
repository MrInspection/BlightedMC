package fr.moussax.blightedMC.smp.core.fishing.registry;

import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.FishingMethod;
import fr.moussax.blightedMC.smp.features.fishing.EndFishing;
import fr.moussax.blightedMC.smp.features.fishing.NetherFishing;
import fr.moussax.blightedMC.smp.features.fishing.OverworldFishing;
import fr.moussax.blightedMC.smp.features.fishing.OverworldLavaFishing;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FishingLootRegistry {

    private static final Map<World.Environment, Map<FishingMethod, FishingLootProvider>> REGISTRY = new EnumMap<>(World.Environment.class);
    private static final FishingLootTable EMPTY_TABLE = FishingLootTable.builder().build();

    private static final List<FishingLootProvider> PROVIDERS = List.of(
        new NetherFishing(),
        new OverworldLavaFishing(),
        new OverworldFishing(),
        new EndFishing()
    );

    private FishingLootRegistry() {
    }

    /**
     * Initializes the registry by clearing existing loot tables and loading all
     * loot tables from providers. Logs the total count of registered loot tables.
     */
    public static void initialize() {
        clear();
        PROVIDERS.forEach(FishingLootProvider::register);
        Log.success("FishingLootRegistry", "Registered " + countRegistrations() + " fishing loot tables.");
    }

    /**
     * Registers a provider for a specific environment and fishing method.
     *
     * @param environment the world environment
     * @param method      the fishing method (WATER or LAVA)
     * @param provider    the loot provider
     */
    static void register(World.Environment environment, FishingMethod method, FishingLootProvider provider) {
        REGISTRY.computeIfAbsent(environment, k -> new EnumMap<>(FishingMethod.class)).put(method, provider);
    }

    /**
     * Retrieves the loot table for a specific environment and method.
     *
     * @param environment the environment
     * @param method      the fishing method
     * @return the FishingLootTable, or an empty table if not found
     */
    public static FishingLootTable getTable(World.Environment environment, FishingMethod method) {
        Map<FishingMethod, FishingLootProvider> envMap = REGISTRY.get(environment);
        if (envMap == null) return EMPTY_TABLE;

        FishingLootProvider provider = envMap.get(method);
        return provider != null ? provider.provide() : EMPTY_TABLE;
    }

    /**
     * Clears all registered fishing loot tables from the registry.
     * <p>
     * Typically used during plugin reloads or shutdown.
     */
    public static void clear() {
        REGISTRY.clear();
    }

    /**
     * Counts the total number of registered environment-method combinations.
     *
     * @return total registration count
     */
    private static int countRegistrations() {
        return REGISTRY.values().stream()
            .mapToInt(Map::size)
            .sum();
    }
}
