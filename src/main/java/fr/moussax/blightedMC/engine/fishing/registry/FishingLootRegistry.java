package fr.moussax.blightedMC.engine.fishing.registry;

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

    private FishingLootRegistry() {
    }

    /**
     * Initializes the registry by clearing existing registrations and loading
     * loot tables from all provided fishing modules.
     *
     * @param modules the list of fishing modules to load
     */
    public static void initialize(List<RegistryModule<FishingRegistryHandler>> modules) {
        clear();
        modules.forEach(module -> module.register(FishingLootRegistry::register));
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
        REGISTRY.computeIfAbsent(environment, _ -> new EnumMap<>(FishingMethod.class)).put(method, table);
    }

    /**
     * Retrieves the fishing loot table registered for the specified environment
     * and method.
     *
     * <p>If no loot table has been registered for the given combination, an
     * empty table is returned.</p>
     *
     * @param environment the environment to look up
     * @param method the fishing method to look up
     * @return the matching loot table, or an empty table if missing
     */
    public static FishingLootTable getTable(World.Environment environment, FishingMethod method) {
        Map<FishingMethod, FishingLootTable> environmentTables = REGISTRY.get(environment);
        if (environmentTables == null) {
            return EMPTY_TABLE;
        }

        return environmentTables.getOrDefault(method, EMPTY_TABLE);
    }

    /**
     * Counts the total number of registered fishing loot tables across all
     * environments and methods.
     *
     * @return the total number of registered loot tables
     */
    public static int countRegistrations() {
        int total = 0;
        for (Map<FishingMethod, FishingLootTable> methodMap : REGISTRY.values()) {
            total += methodMap.size();
        }
        return total;
    }

    /**
     * Clears all registered fishing loot tables.
     */
    public static void clear() {
        REGISTRY.clear();
    }
}
