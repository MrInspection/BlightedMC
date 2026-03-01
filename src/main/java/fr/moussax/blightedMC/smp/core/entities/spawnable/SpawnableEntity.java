package fr.moussax.blightedMC.smp.core.entities.spawnable;

import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnCondition;
import fr.moussax.blightedMC.smp.core.entities.spawnable.engine.SpawnMode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Defines spawning rules and probability for a blighted entity type.
 *
 * <p>Layered on top of {@link AbstractBlightedEntity}, adding spawn conditions,
 * probability, and spawn mode. Subclasses define their conditions by overriding
 * {@link #defineSpawnConditions()}.</p>
 *
 * <pre>{@code
 * public class BlightedZombie extends SpawnableEntity {
 *     public BlightedZombie() {
 *         super("blighted_zombie", "Blighted Zombie", 40, EntityType.ZOMBIE, 0.15);
 *     }
 *
 *     @Override
 *     protected void defineSpawnConditions() {
 *         addCondition(SpawnRules.nightTime());
 *         addCondition(SpawnRules.maxLightLevel(7));
 *     }
 * }
 * }</pre>
 */
public abstract class SpawnableEntity extends AbstractBlightedEntity {

    @Getter
    private final double spawnProbability;
    @Getter
    private final SpawnMode spawnMode;
    private SpawnProfile spawnProfile;

    protected SpawnableEntity(
        String entityId,
        String name,
        int maxHealth,
        EntityType entityType,
        double probability
    ) {
        this(entityId, name, maxHealth, 1, 0, entityType, probability, SpawnMode.REPLACEMENT);
    }

    protected SpawnableEntity(
        String entityId,
        String name,
        int maxHealth,
        EntityType entityType,
        double probability,
        SpawnMode mode
    ) {
        this(entityId, name, maxHealth, 1, 0, entityType, probability, mode);
    }

    protected SpawnableEntity(
        String entityId,
        String name,
        int maxHealth,
        int damage,
        EntityType entityType,
        double probability,
        SpawnMode mode
    ) {
        this(entityId, name, maxHealth, damage, 0, entityType, probability, mode);
    }

    protected SpawnableEntity(
        String entityId,
        String name,
        int maxHealth,
        int damage,
        int defense,
        EntityType entityType,
        double probability,
        SpawnMode mode
    ) {
        super(name, maxHealth, damage, defense, entityType);
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("spawnProbability must be in [0.0, 1.0], got: " + probability);
        }
        // entityId is declared in AbstractBlightedEntity — assign it directly
        this.entityId = entityId;
        this.spawnProbability = probability;
        this.spawnMode = mode;
        this.spawnProfile = new SpawnProfile();
        defineSpawnConditions();
    }

    /**
     * Called during construction. Override to register spawn conditions via {@link #addCondition}.
     */
    protected abstract void defineSpawnConditions();

    /**
     * Adds a spawn condition. All conditions must pass for spawning to be allowed.
     */
    protected void addCondition(SpawnCondition condition) {
        spawnProfile.addCondition(condition);
    }

    /**
     * Evaluates whether this entity may spawn at the given location.
     */
    public boolean canSpawnAt(Location location, World world) {
        return spawnProfile.canSpawn(location, world);
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public SpawnableEntity clone() {
        SpawnableEntity cloned = (SpawnableEntity) super.clone();
        cloned.spawnProfile = this.spawnProfile.copy();
        return cloned;
    }
}
