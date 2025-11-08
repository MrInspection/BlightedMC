package fr.moussax.blightedMC.core.entities.listeners;

import fr.moussax.blightedMC.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.core.entities.registry.SpawnableEntitiesRegistry;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SpawnableEntitiesListener implements Listener {

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (!isEligibleSpawnReason(event.getSpawnReason())) {
      return;
    }

    Location spawnLocation = event.getLocation();
    World world = spawnLocation.getWorld();
    if (world == null) return;

    List<SpawnableEntity> eligibleEntities = getEligibleEntities(spawnLocation, world);
    if (eligibleEntities.isEmpty()) return;

    SpawnableEntity selectedEntity = selectRandomEntity(eligibleEntities);
    if (selectedEntity == null) return;

    spawnCustomEntity(selectedEntity, spawnLocation);
    event.setCancelled(true);
  }

  private boolean isEligibleSpawnReason(CreatureSpawnEvent.SpawnReason reason) {
    return reason == CreatureSpawnEvent.SpawnReason.NATURAL
      || reason == CreatureSpawnEvent.SpawnReason.REINFORCEMENTS;
  }

  private List<SpawnableEntity> getEligibleEntities(Location location, World world) {
    List<SpawnableEntity> eligible = new ArrayList<>();
    List<SpawnableEntity> allEntities = SpawnableEntitiesRegistry.getAllEntities();

    for (SpawnableEntity entity : allEntities) {
      if (entity.canSpawnAt(location, world)) {
        eligible.add(entity);
      }
    }
    return eligible;
  }

  private SpawnableEntity selectRandomEntity(List<SpawnableEntity> eligibleEntities) {
    for (SpawnableEntity entity : eligibleEntities) {
      if (ThreadLocalRandom.current().nextDouble() <= entity.getSpawnChance()) {
        return entity;
      }
    }
    return null;
  }

  private void spawnCustomEntity(SpawnableEntity entity, Location location) {
    try {
      entity.spawn(location);
    } catch (Exception e) {
      Log.error("Failed to spawn custom entity " + entity.getEntityId() + ": " + e.getMessage());
    }
  }
}