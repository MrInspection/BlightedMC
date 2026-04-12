package fr.moussax.blightedMC.engine.entities.spawnable;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnCondition;
import fr.moussax.blightedMC.engine.entities.spawnable.engine.SpawnMode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

/**
 * Defines spawning rules and probability for a blighted entity type.
 *
 * <p>Layered on top of {@link BlightedEntity}, adding spawn conditions,
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
public abstract class SpawnableEntity extends BlightedEntity {

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

        this.entityId = entityId;
        this.spawnProbability = probability;
        this.spawnMode = mode;
        this.spawnProfile = new SpawnProfile();
        defineSpawnConditions();
    }

    protected abstract void defineSpawnConditions();

    protected void addCondition(SpawnCondition condition) {
        spawnProfile.addCondition(condition);
    }

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
