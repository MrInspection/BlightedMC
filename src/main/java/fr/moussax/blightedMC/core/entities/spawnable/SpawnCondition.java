package fr.moussax.blightedMC.core.entities.spawnable;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Functional interface defining a condition that determines whether an entity
 * can spawn at a given {@link Location} in a {@link World}.
 * <p>
 * Spawn conditions can be composed using logical operations:
 * <ul>
 *   <li>{@link #and(SpawnCondition)} — requires both conditions to be true</li>
 *   <li>{@link #or(SpawnCondition)} — requires at least one condition to be true</li>
 *   <li>{@link #not()} — inverts the result of the condition</li>
 * </ul>
 * <p>
 * Example:
 * <pre>{@code
 * SpawnCondition nearSurface = (loc, world) -> loc.getBlockY() > 60;
 * SpawnCondition notInNether = (loc, world) -> world.getEnvironment() != World.Environment.NETHER;
 *
 * SpawnCondition validSpawn = nearSurface.and(notInNether);
 * }</pre>
 */
@FunctionalInterface
public interface SpawnCondition {
  /**
   * Determines whether an entity can spawn at the specified location in the given world.
   *
   * @param location the target spawn location
   * @param world the world in which spawning is attempted
   * @return {@code true} if spawning is allowed, {@code false} otherwise
   */
  boolean canSpawn(Location location, World world);

  /**
   * Combines this condition with another using logical AND.
   * <p>
   * The resulting condition is true only if both conditions are true.
   *
   * @param other the other condition to combine with
   * @return a new {@link SpawnCondition} representing the logical AND
   */
  default SpawnCondition and(SpawnCondition other) {
    return (loc, world) -> this.canSpawn(loc, world) && other.canSpawn(loc, world);
  }

  /**
   * Combines this condition with another using logical OR.
   * <p>
   * The resulting condition is true if at least one condition is true.
   *
   * @param other the other condition to combine with
   * @return a new {@link SpawnCondition} representing the logical OR
   */
  default SpawnCondition or(SpawnCondition other) {
    return (loc, world) -> this.canSpawn(loc, world) || other.canSpawn(loc, world);
  }

  /**
   * Negates this spawn condition.
   * <p>
   * The resulting condition is true if and only if this one is false.
   *
   * @return a new {@link SpawnCondition} representing the logical negation
   */
  default SpawnCondition not() {
    return (loc, world) -> !this.canSpawn(loc, world);
  }
}
