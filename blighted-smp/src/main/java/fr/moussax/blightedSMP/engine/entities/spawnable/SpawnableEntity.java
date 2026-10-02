package fr.moussax.blightedSMP.engine.entities.spawnable;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.components.AffixRegistry;
import fr.moussax.blightedSMP.engine.entities.components.EntityComponent;
import fr.moussax.blightedSMP.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedSMP.engine.entities.spawnable.condition.SpawnCondition;
import fr.moussax.blightedSMP.engine.entities.spawnable.engine.SpawnMode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for custom entities capable of spawning naturally in the world.
 *
 * <p>Extends {@link BlightedEntity} with spawn probabilities, spawn evaluation rules
 * ({@link SpawnProfile}), spawn modes ({@link SpawnMode}), and optional elite affix rolling.</p>
 */
public abstract class SpawnableEntity extends BlightedEntity {

    /**
     * Persistent data key storing comma-separated active affix IDs assigned to this entity.
     */
    public static final NamespacedKey AFFIXES_KEY =
            new NamespacedKey(BlightedSMP.getInstance(), "blighted_active_affix");

    @Getter
    private double spawnProbability;

    @Getter
    private SpawnMode spawnMode;

    private SpawnProfile spawnProfile;

    /**
     * Sets the natural spawn probability in the range {@code [0.0, 1.0]}.
     *
     * @param spawnProbability probability in [0.0, 1.0]
     * @throws IllegalArgumentException if outside [0.0, 1.0]
     */
    public void setSpawnProbability(double spawnProbability) {
        if (spawnProbability < 0.0 || spawnProbability > 1.0) {
            throw new IllegalArgumentException("spawnProbability must be in [0.0, 1.0], got: " + spawnProbability);
        }
        this.spawnProbability = spawnProbability;
    }

    /**
     * Sets the spawn mode.
     *
     * @param spawnMode spawn mode
     */
    public void setSpawnMode(@NonNull SpawnMode spawnMode) {
        this.spawnMode = Objects.requireNonNull(spawnMode, "spawnMode cannot be null");
    }

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

    public static final double DEFAULT_SPAWN_PROBABILITY = 0.01;
    public static final SpawnMode DEFAULT_SPAWN_MODE = SpawnMode.REPLACEMENT;

    /**
     * Constructs a spawnable entity with basic identity requirements.
     * Default spawn probability is 0.01 in REPLACEMENT mode.
     *
     * @param entityId   unique entity identifier
     * @param name       display name
     * @param entityType underlying Minecraft entity type
     */
    protected SpawnableEntity(@NonNull String entityId, @NonNull String name, @NonNull EntityType entityType) {
        super(entityId, name, entityType);
        this.spawnProbability = DEFAULT_SPAWN_PROBABILITY;
        this.spawnMode = DEFAULT_SPAWN_MODE;
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

                spawned.getPersistentDataContainer()
                        .set(AFFIXES_KEY, PersistentDataType.STRING, String.join(",", affixIds));
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
            Location orbitLocation = center.clone().add(x, 0, z);

            world.spawnParticle(Particle.SCULK_SOUL, orbitLocation, 1, 0.05, 0.05, 0.05, 0.02);

            if (time % 10 == 0) {
                world.spawnParticle(Particle.ENCHANT, center, 5, 0.5, 0.5, 0.5, 0.01);
            }
        });
    }

    /**
     * Hook method implemented by subclasses to register environment spawn conditions.
     * Invoked lazily when the spawn profile is first needed.
     */
    protected void defineSpawnConditions() {
    }

    private void ensureSpawnProfile() {
        if (spawnProfile == null) {
            spawnProfile = new SpawnProfile();
            defineSpawnConditions();
        }
    }

    /**
     * Adds a spawn condition rule to this entity's spawn profile.
     *
     * @param condition spawn condition rule to add
     */
    protected void addCondition(@NonNull SpawnCondition condition) {
        if (spawnProfile == null) {
            spawnProfile = new SpawnProfile();
        }
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
        ensureSpawnProfile();
        return spawnProfile.canSpawn(location, world);
    }

    @NonNull
    @Override
    public SpawnableEntity createInstance() {
        SpawnableEntity fresh = EntitiesRegistry.createSpawnable(getEntityId());
        if (fresh != null) {
            return fresh;
        }
        return (SpawnableEntity) super.createInstance();
    }
}
