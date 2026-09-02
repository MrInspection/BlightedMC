package fr.moussax.blightedSMP.engine.entities.spawnable;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.components.AffixRegistry;
import fr.moussax.blightedSMP.engine.entities.components.EntityComponent;
import fr.moussax.blightedSMP.engine.entities.spawnable.condition.SpawnCondition;
import fr.moussax.blightedSMP.engine.entities.spawnable.engine.SpawnMode;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for custom entities capable of spawning naturally in the world.
 *
 * <p>Extends {@link BlightedEntity} with spawn probabilities, spawn evaluation rules
 * ({@link SpawnProfile}), spawn modes ({@link SpawnMode}), and optional elite affix rolling.</p>
 */
public abstract class SpawnableEntity extends BlightedEntity {

    /** Persistent data key storing comma-separated active affix IDs assigned to this entity. */
    public static final NamespacedKey AFFIXES_KEY = new NamespacedKey(BlightedSMP.getInstance(), "blighted_active_affix");

    @Getter
    private final double spawnProbability;
    @Getter
    private final SpawnMode spawnMode;
    private SpawnProfile spawnProfile;

    @Getter
    private double affixChance = 0.0;
    @Getter
    private int maxAffixes = 1;

    /**
     * Sets the chance for this entity to spawn with random elite affixes.
     *
     * @param affixChance probability in the range {@code [0.0, 1.0]}
     * @throws IllegalArgumentException if {@code affixChance} is outside {@code [0.0, 1.0]}
     */
    public void setAffixChance(double affixChance) {
        if (affixChance < 0.0 || affixChance > 1.0) {
            throw new IllegalArgumentException("affixChance must be in [0.0, 1.0], got: " + affixChance);
        }
        this.affixChance = affixChance;
    }

    /**
     * Sets the maximum number of elite affixes this entity can roll when spawned.
     *
     * @param maxAffixes maximum affix count
     */
    public void setMaxAffixes(int maxAffixes) {
        this.maxAffixes = Math.max(1, maxAffixes);
    }

    /**
     * Constructs a spawnable entity with default damage and defense attributes in replacement mode.
     *
     * @param entityId    unique entity identifier
     * @param name        display name
     * @param maxHealth   maximum health
     * @param entityType  underlying Minecraft entity type
     * @param probability spawn probability in range {@code [0.0, 1.0]}
     */
    protected SpawnableEntity(String entityId, String name, int maxHealth, EntityType entityType, double probability) {
        this(entityId, name, maxHealth, 1, 0, entityType, probability, SpawnMode.REPLACEMENT);
    }

    /**
     * Constructs a spawnable entity with default damage and defense attributes in the specified spawn mode.
     *
     * @param entityId    unique entity identifier
     * @param name        display name
     * @param maxHealth   maximum health
     * @param entityType  underlying Minecraft entity type
     * @param probability spawn probability in range {@code [0.0, 1.0]}
     * @param mode        spawn mode
     */
    protected SpawnableEntity(String entityId, String name, int maxHealth, EntityType entityType, double probability, SpawnMode mode) {
        this(entityId, name, maxHealth, 1, 0, entityType, probability, mode);
    }

    /**
     * Constructs a spawnable entity with custom attack damage and zero defense in the specified spawn mode.
     *
     * @param entityId    unique entity identifier
     * @param name        display name
     * @param maxHealth   maximum health
     * @param damage      base attack damage
     * @param entityType  underlying Minecraft entity type
     * @param probability spawn probability in range {@code [0.0, 1.0]}
     * @param mode        spawn mode
     */
    protected SpawnableEntity(String entityId, String name, int maxHealth, int damage, EntityType entityType, double probability, SpawnMode mode) {
        this(entityId, name, maxHealth, damage, 0, entityType, probability, mode);
    }

    /**
     * Constructs a spawnable entity with full attribute specifications and spawn profile initialization.
     *
     * @param entityId    unique entity identifier
     * @param name        display name
     * @param maxHealth   maximum health
     * @param damage      base attack damage
     * @param defense     base armor defense
     * @param entityType  underlying Minecraft entity type
     * @param probability spawn probability in range {@code [0.0, 1.0]}
     * @param mode        spawn mode
     * @throws IllegalArgumentException if {@code probability} is outside {@code [0.0, 1.0]}
     */
    protected SpawnableEntity(String entityId, String name, int maxHealth, int damage, int defense, EntityType entityType, double probability, SpawnMode mode) {
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

    /**
     * Spawns this entity at the specified location and rolls for elite affix assignment.
     *
     * @param location target spawn location
     * @return spawned living entity
     */
    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity spawned = super.spawn(location);

        if (affixChance > 0.0 && Math.random() <= affixChance) {
            List<EntityComponent> rolledAffixes = AffixRegistry.getRandomAffixes(maxAffixes);
            if (!rolledAffixes.isEmpty()) {
                List<String> affixIds = new ArrayList<>();
                for (EntityComponent affix : rolledAffixes) {
                    affixIds.add(affix.getId());
                    applyAffix(affix);
                }

                spawned.getPersistentDataContainer().set(AFFIXES_KEY, PersistentDataType.STRING, String.join(",", affixIds));
            }
        }

        return spawned;
    }

    /**
     * Rehydrates an existing entity state, restoring persistent affixes if present.
     *
     * @param existing existing living entity instance in the world
     */
    @Override
    protected void onRehydrate(LivingEntity existing) {
        super.onRehydrate(existing);

        String persistentAffixes = existing.getPersistentDataContainer().get(AFFIXES_KEY, PersistentDataType.STRING);
        if (persistentAffixes != null && !persistentAffixes.isEmpty()) {
            String[] affixIds = persistentAffixes.split(",");
            for (String affixId : affixIds) {
                if (getComponent(affixId) == null) {
                    EntityComponent affix = AffixRegistry.getAffixById(affixId.trim());
                    if (affix != null) {
                        applyAffix(affix);
                    }
                }
            }
        }
    }

    private boolean eliteAuraStarted = false;

    private void applyAffix(EntityComponent affix) {
        addComponent(affix);
        if (!eliteAuraStarted) {
            eliteAuraStarted = true;
            startEliteAura();
        }
    }

    private void startEliteAura() {
        addCoreAbility(5L, 3L, () -> {
            if (!isAlive()) return;

            long time = entity.getTicksLived();
            Location center = entity.getLocation().add(0, entity.getHeight() / 2.0, 0);
            World world = entity.getWorld();

            double angle = time * 0.2;
            double x = Math.cos(angle) * 0.6;
            double z = Math.sin(angle) * 0.6;
            Location orbitLoc = center.clone().add(x, 0, z);

            world.spawnParticle(Particle.SCULK_SOUL, orbitLoc, 1, 0.05, 0.05, 0.05, 0.02);

            if (time % 10 == 0) {
                world.spawnParticle(Particle.ENCHANT, center, 5, 0.5, 0.5, 0.5, 0.01);
            }
        });
    }

    /**
     * Hook method implemented by subclasses to register environment spawn conditions.
     */
    protected abstract void defineSpawnConditions();

    /**
     * Adds a spawn condition rule to this entity's spawn profile.
     *
     * @param condition spawn condition rule to add
     */
    protected void addCondition(SpawnCondition condition) {
        spawnProfile.addCondition(condition);
    }

    /**
     * Evaluates whether this entity can spawn at the specified location and world.
     *
     * @param location location to evaluate
     * @param world    world to evaluate
     * @return {@code true} if all spawn profile conditions are satisfied, {@code false} otherwise
     */
    public boolean canSpawnAt(Location location, World world) {
        return spawnProfile.canSpawn(location, world);
    }

    /**
     * Creates an independent clone of this spawnable entity definition.
     *
     * @return cloned entity definition
     */
    @Override
    public SpawnableEntity clone() {
        SpawnableEntity cloned = (SpawnableEntity) super.clone();
        cloned.spawnProfile = this.spawnProfile != null ? this.spawnProfile.copy() : new SpawnProfile();
        return cloned;
    }
}
