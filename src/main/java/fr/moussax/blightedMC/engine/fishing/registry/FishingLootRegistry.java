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

/**
 * Registry for fishing loot tables organized by world environment and
 * fishing method.
 *
 * <p>Loot tables are provided by registered {@link RegistryModule} instances
 * and can be retrieved using their associated environment and fishing method.
 * Missing registrations resolve to an empty loot table.</p>
 */
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

    /**
     * Initializes the registry by clearing existing registrations and loading
     * loot tables from all configured fishing providers.
     */
    public static void initialize() {
        clear();
        PROVIDERS.forEach(module -> module.register(FishingLootRegistry::register));
        Log.success("FishingLootRegistry", "Registered " + countRegistrations() + " fishing loot tables.");
    }

    /**
     * Registers a fishing loot table for the specified environment and method.
     *
     * <p>An existing registration for the same environment and method is
     * replaced.</p>
     *
     * @param environment the world environment associated with the table
     * @param method the fishing method associated with the table
     * @param table the loot table to register
     */
    public static void register(World.Environment environment, FishingMethod method, FishingLootTable table) {
        REGISTRY.computeIfAbsent(environment, k -> new EnumMap<>(FishingMethod.class)).put(method, table);
    }

    /**
     * Returns the fishing loot table registered for the specified environment
     * and fishing method.
     *
     * <p>Returns an empty loot table when no registration exists for the
     * specified combination.</p>
     *
     * @param environment the world environment to query
     * @param method the fishing method to query
     * @return the registered loot table, or an empty loot table if none exists
     */
    public static FishingLootTable getTable(World.Environment environment, FishingMethod method) {
        Map<FishingMethod, FishingLootTable> envMap = REGISTRY.get(environment);
        if (envMap == null) return EMPTY_TABLE;

        FishingLootTable table = envMap.get(method);
        return table != null ? table : EMPTY_TABLE;
    }

    /**
     * Removes all registered fishing loot tables.
     */
    public static void clear() {
        REGISTRY.clear();
    }

    private static int countRegistrations() {
        return REGISTRY.values().stream()
                .mapToInt(Map::size)
                .sum();
    }
}
