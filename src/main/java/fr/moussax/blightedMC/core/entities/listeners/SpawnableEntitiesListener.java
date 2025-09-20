package fr.moussax.blightedMC.core.entities.listeners;

import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntitiesRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;
import java.util.Random;

public class SpawnableEntitiesListener implements Listener {
  private final Random randomizer = new Random();

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    switch (event.getSpawnReason()) {
      case NATURAL:
      case CHUNK_GEN:
      case REINFORCEMENTS:
        break;
      default:
        return; // Don't interfere with other spawn reasons
    }

    Location spawnLocation = event.getLocation();
    World world = spawnLocation.getWorld();
    if (world == null) return;

    List<SpawnableEntity> spawnableEntities = SpawnableEntitiesRegistry.getAllEntities();
    if (spawnableEntities.isEmpty()) return;
    for (SpawnableEntity spawnableEntity : spawnableEntities) {
      if (shouldSpawnCustomEntity(spawnableEntity, spawnLocation, world)) {
        spawnCustomEntity(spawnableEntity, spawnLocation);
      }
    }
  }

  private boolean shouldSpawnCustomEntity(SpawnableEntity entity, Location location, World world) {
    if (!entity.canSpawnAt(location, world)) {
      return false;
    }

    return randomizer.nextDouble() <= entity.getSpawnChance();
  }

  private void spawnCustomEntity(SpawnableEntity entity, Location location) {
    try {
      entity.spawn(location);
    } catch (Exception e) {
      System.err.println("Failed to spawn custom entity " + entity.getEntityId() + ": " + e.getMessage());
    }
  }
}
