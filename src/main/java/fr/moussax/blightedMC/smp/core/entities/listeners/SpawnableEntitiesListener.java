package fr.moussax.blightedMC.smp.core.entities.listeners;

import fr.moussax.blightedMC.smp.core.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SpawnableEntitiesListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason != CreatureSpawnEvent.SpawnReason.NATURAL &&
            reason != CreatureSpawnEvent.SpawnReason.REINFORCEMENTS) {
            return;
        }

        Location location = event.getLocation();
        World world = location.getWorld();
        if (world == null) return;

        List<SpawnableEntity> eligible = new ArrayList<>();
        for (SpawnableEntity entity : SpawnableEntitiesRegistry.getAllEntities()) {
            if (entity.canSpawnAt(location, world)) {
                eligible.add(entity);
            }
        }

        if (eligible.isEmpty()) return;

        double totalChance = 0.0;
        for (SpawnableEntity entity : eligible) {
            totalChance += entity.getSpawnProbability();
        }
        totalChance = Math.min(totalChance, 1.0);

        if (ThreadLocalRandom.current().nextDouble() >= totalChance) return;

        double roll = ThreadLocalRandom.current().nextDouble() * totalChance;
        double cumulative = 0.0;

        for (SpawnableEntity entity : eligible) {
            cumulative += entity.getSpawnProbability();
            if (roll < cumulative) {
                event.setCancelled(true);
                entity.spawn(location, CreatureSpawnEvent.SpawnReason.CUSTOM);
                return;
            }
        }
    }
}
