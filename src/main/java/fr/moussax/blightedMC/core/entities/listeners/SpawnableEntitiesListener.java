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
    // Only handle natural spawning reasons
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

    // Get all registered spawnable entities
    List<SpawnableEntity> spawnableEntities = SpawnableEntitiesRegistry.getAllEntities();
    
    if (spawnableEntities.isEmpty()) return;

    // Try to spawn custom entities at the same location
    for (SpawnableEntity spawnableEntity : spawnableEntities) {
      if (shouldSpawnCustomEntity(spawnableEntity, spawnLocation, world)) {
        spawnCustomEntity(spawnableEntity, spawnLocation);
      }
    }
  }

  /**
   * Determines if a custom entity should spawn based on conditions and chance.
   *
   * @param entity the spawnable entity to check
   * @param location the spawn location
   * @param world the world
   * @return true if the entity should spawn
   */
  private boolean shouldSpawnCustomEntity(SpawnableEntity entity, Location location, World world) {
    // Check if the entity can spawn at this location based on its conditions
    if (!entity.canSpawnAt(location, world)) {
      return false;
    }

    // Check spawn chance
    return randomizer.nextDouble() <= entity.getSpawnChance();
  }

  /**
   * Spawns a custom entity at the specified location.
   *
   * @param entity the spawnable entity to spawn
   * @param location the location to spawn at
   */
  private void spawnCustomEntity(SpawnableEntity entity, Location location) {
    try {
      // Spawn the entity using the vanilla spawn method
      entity.spawn(location);
    } catch (Exception e) {
      // Log error but don't crash the server
      System.err.println("Failed to spawn custom entity " + entity.getEntityId() + ": " + e.getMessage());
    }
  }
}
