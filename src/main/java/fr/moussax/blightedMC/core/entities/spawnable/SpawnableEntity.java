package fr.moussax.blightedMC.core.entities.spawnable;

import fr.moussax.blightedMC.core.entities.AbstractBlightedEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Defines spawning rules and probability for a blighted entity type.
 * <p>
 * Acts as a spawn definition layered on top of {@link AbstractBlightedEntity},
 * providing spawn conditions and spawn probability while delegating
 * runtime entity behavior to the base implementation.
 *
 * <p>Subclasses define their spawn rules by overriding
 * {@link #defineSpawnConditions()}.
 */
public abstract class SpawnableEntity extends AbstractBlightedEntity {
    private final String entityId;
    private final double spawnProbability;
    private SpawnProfile spawnProfile;

    /**
     * Creates a spawn definition for a blighted entity type.
     *
     * @param entityId    unique identifier for the entity type
     * @param name        display name
     * @param maxHealth   maximum health value
     * @param entityType  underlying Bukkit {@link EntityType}
     * @param probability spawn probability in the range {@code [0.0, 1.0]}
     */
    protected SpawnableEntity(String entityId, String name, int maxHealth, EntityType entityType, double probability) {
        super(name, maxHealth, entityType);
        this.entityId = entityId;
        this.spawnProbability = probability;
        this.spawnProfile = new SpawnProfile();
        defineSpawnConditions();
    }

    /**
     * Defines the spawn conditions for this entity type.
     * Called during construction.
     */
    protected abstract void defineSpawnConditions();

    /**
     * Adds a spawn condition to this definition.
     *
     * @param condition spawn condition to add
     */
    protected void addCondition(SpawnCondition condition) {
        spawnProfile.addSpawnCondition(condition);
    }

    /**
     * Evaluates whether this entity type may spawn at the given location.
     *
     * @param location target location
     * @param world    target world
     * @return {@code true} if spawning is permitted
     */
    public boolean canSpawnAt(Location location, World world) {
        return spawnProfile.canSpawn(location, world);
    }

    /**
     * @return spawn probability in the range {@code [0.0, 1.0]}
     */
    public double getSpawnProbability() {
        return spawnProbability;
    }

    /**
     * @return total number of registered spawn conditions
     */
    public int getConditionCount() {
        return spawnProfile.getConditionCount();
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    /**
     * Spawns a runtime instance of this entity type at the given location
     * using {@link CreatureSpawnEvent.SpawnReason#CUSTOM}.
     *
     * @param location spawn location
     * @return spawned {@link LivingEntity}
     */
    @Override
    public LivingEntity spawn(Location location) {
        return spawn(location, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    /**
     * Spawns a runtime instance of this entity type at the given location.
     *
     * @param location spawn location
     * @param reason   spawn event reason
     * @return spawned {@link LivingEntity}
     */
    public LivingEntity spawn(Location location, CreatureSpawnEvent.SpawnReason reason) {
        return super.spawn(location);
    }

    /**
     * Creates a copy of this spawn definition, including its spawn conditions.
     *
     * @return copied {@link SpawnableEntity} definition
     */
    @Override
    public SpawnableEntity clone() {
        SpawnableEntity cloned = (SpawnableEntity) super.clone();
        cloned.spawnProfile = this.spawnProfile.copy();
        return cloned;
    }
}
