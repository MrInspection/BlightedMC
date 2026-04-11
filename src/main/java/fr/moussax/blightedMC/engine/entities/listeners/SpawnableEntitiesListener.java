package fr.moussax.blightedMC.engine.entities.listeners;

import fr.moussax.blightedMC.engine.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.engine.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.engine.entities.spawnable.engine.SpawnMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class SpawnableEntitiesListener implements Listener {

    private volatile Map<EntityType, List<SpawnableEntity>> spawnCache = Collections.emptyMap();
    private volatile boolean cacheInitialized = false;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!cacheInitialized) rebuildCache();

        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason != CreatureSpawnEvent.SpawnReason.NATURAL
            && reason != CreatureSpawnEvent.SpawnReason.REINFORCEMENTS) {
            return;
        }

        List<SpawnableEntity> candidates = spawnCache.get(event.getEntityType());
        if (candidates == null || candidates.isEmpty()) return;

        Location location = event.getLocation();
        World world = location.getWorld();
        if (world == null) return;

        List<SpawnableEntity> eligible = null;
        for (SpawnableEntity entity : candidates) {
            if (!entity.canSpawnAt(location, world)) continue;
            if (eligible == null) eligible = new ArrayList<>(candidates.size());
            eligible.add(entity);
        }

        if (eligible == null) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // First roll: does any replacement spawn happen at all?
        double totalChance = 0.0;
        for (SpawnableEntity entity : eligible) {
            totalChance += entity.getSpawnProbability();
        }

        if (random.nextDouble() >= Math.min(totalChance, 1.0)) return;

        // Second roll: which entity wins, weighted by individual probability.
        double selectionRoll = random.nextDouble() * totalChance;
        double cumulative = 0.0;
        for (SpawnableEntity entity : eligible) {
            cumulative += entity.getSpawnProbability();
            if (selectionRoll < cumulative) {
                event.setCancelled(true);
                entity.clone().spawn(location);
                return;
            }
        }
    }

    public synchronized void rebuildCache() {
        if (cacheInitialized) return;

        Map<EntityType, List<SpawnableEntity>> newCache = new EnumMap<>(EntityType.class);
        for (SpawnableEntity entity : SpawnableEntitiesRegistry.getAll()) {
            SpawnMode mode = entity.getSpawnMode();
            if (mode == SpawnMode.REPLACEMENT || mode == SpawnMode.HYBRID) {
                newCache.computeIfAbsent(entity.getEntityType(), k -> new ArrayList<>()).add(entity);
            }
        }

        this.spawnCache = newCache;
        this.cacheInitialized = true;
    }

    public void invalidateCache() {
        cacheInitialized = false;
    }
}
