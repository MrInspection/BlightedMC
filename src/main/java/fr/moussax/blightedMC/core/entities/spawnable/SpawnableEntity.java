package fr.moussax.blightedMC.core.entities.spawnable;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Represents a custom entity that can spawn in the world based on specific conditions.
 * <p>
 * A {@code SpawnableEntity} extends {@link BlightedEntity} and introduces spawning logic,
 * including configurable {@link SpawnCondition}s and spawn chance probability.
 *
 * <p>Concrete implementations must define spawn conditions by overriding
 * {@link #setupSpawnConditions()}, typically using utility methods from
 * {@link SpawnConditions} to control where and when the entity may appear.
 *
 * <p>Example:</p>
 * <pre>{@code
 * public class BlightedZombie extends SpawnableEntity {
 *   public BlightedZombie() {
 *     super("BLIGHTED_ZOMBIE", "Blighted Zombie", 40, EntityType.ZOMBIE, 0.2);
 *   }
 *
 *   @Override
 *   protected void setupSpawnConditions() {
 *     addSpawnCondition(SpawnConditions.biome(Biome.PLAINS, Biome.FOREST));
 *     addSpawnCondition(SpawnConditions.nightTime());
 *     addSpawnCondition(SpawnConditions.notInWater());
 *   }
 * }
 * }</pre>
 */
public abstract class SpawnableEntity extends BlightedEntity {
    private final String entityId;
    private final double spawnChance;
    private SpawnableEntityProfile spawnProfile;

    /**
     * Constructs a new spawnable entity.
     *
     * @param entityId    unique identifier for this entity type
     * @param name        display name of the entity
     * @param maxHealth   maximum health value
     * @param entityType  Bukkit {@link EntityType} of the underlying entity
     * @param spawnChance probability of spawning (0.0–1.0)
     */
    protected SpawnableEntity(String entityId, String name, int maxHealth, EntityType entityType, double spawnChance) {
        super(name, maxHealth, entityType);
        this.entityId = entityId;
        this.spawnChance = spawnChance;
        this.spawnProfile = new SpawnableEntityProfile();
        setupSpawnConditions();
    }

    /**
     * Defines the spawn conditions specific to this entity type.
     * <p>
     * This method must be implemented by subclasses and typically calls
     * {@link #addSpawnCondition(SpawnCondition)} several times.
     */
    protected abstract void setupSpawnConditions();

    /**
     * Adds a new spawn condition to this entity’s profile.
     *
     * @param condition the {@link SpawnCondition} to add
     */
    protected void addSpawnCondition(SpawnCondition condition) {
        spawnProfile.addCondition(condition);
    }

    /**
     * Checks if the entity can spawn at the given location in the specified world.
     * <p>
     * All registered conditions must be satisfied for spawning to be allowed.
     *
     * @param location the location to check
     * @param world    the world context
     * @return {@code true} if spawning is allowed, {@code false} otherwise
     */
    public boolean canSpawnAt(Location location, World world) {
        return spawnProfile.canSpawn(location, world);
    }

    /**
     * Returns the probability of this entity spawning.
     *
     * @return spawn chance between 0.0 and 1.0
     */
    public double getSpawnChance() {
        return spawnChance;
    }

    /**
     * Returns the total number of registered spawn conditions.
     *
     * @return the number of conditions
     */
    public int getConditionCount() {
        return spawnProfile.getConditionCount();
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    /**
     * Spawns the entity at the given location with {@link CreatureSpawnEvent.SpawnReason#CUSTOM}.
     *
     * @param location the spawn location
     * @return the spawned {@link LivingEntity}
     */
    @Override
    public LivingEntity spawn(Location location) {
        return spawn(location, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    /**
     * Spawns the entity at the given location with the specified spawn reason.
     *
     * @param location the spawn location
     * @param reason   the reason for the spawn event
     * @return the spawned {@link LivingEntity}
     */
    public LivingEntity spawn(Location location, CreatureSpawnEvent.SpawnReason reason) {
        return super.spawn(location);
    }

    /**
     * Creates a deep copy of this spawnable entity, including its spawn conditions.
     *
     * @return a cloned instance of this {@link SpawnableEntity}
     */
    @Override
    public SpawnableEntity clone() {
        SpawnableEntity cloned = (SpawnableEntity) super.clone();
        cloned.spawnProfile = this.spawnProfile.deepCopy();
        return cloned;
    }
}
