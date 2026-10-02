package fr.moussax.blightedSMP.engine.entities.spawnable;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.components.AffixRegistry;
import fr.moussax.blightedSMP.engine.entities.components.EntityComponent;
import fr.moussax.blightedSMP.engine.entities.registry.EntitiesRegistry;
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
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Abstract base class for custom entities capable of spawning naturally in the world.
 *
 * <p>Extends {@link BlightedEntity} with spawn probabilities, spawn evaluation rules
 * ({@link SpawnProfile}), spawn modes ({@link SpawnMode}), and elite affix mechanics.</p>
 */
public abstract class SpawnableEntity extends BlightedEntity {

    private static NamespacedKey affixesKey;

    public static NamespacedKey getAffixesKey() {
        if (affixesKey == null) {
            affixesKey = new NamespacedKey(BlightedSMP.getInstance(), "blighted_active_affix");
        }
        return affixesKey;
    }

    public static final double DEFAULT_SPAWN_PROBABILITY = SpawningBuilder.DEFAULT_PROBABILITY;
    public static final SpawnMode DEFAULT_SPAWN_MODE = SpawningBuilder.DEFAULT_MODE;

    @Getter
    private double spawnProbability = DEFAULT_SPAWN_PROBABILITY;

    @Getter
    private SpawnMode spawnMode = DEFAULT_SPAWN_MODE;

    @Getter
    private double affixChance = 0.0;

    @Getter
    private int maxAffixes = 1;

    private boolean eliteAuraStarted = false;

    @Getter
    private SpawnProfile spawnProfile = new SpawnProfile();

    /**
     * Constructs a spawnable entity with identity requirements.
     * The default spawn probability is 0.01 in REPLACEMENT mode.
     *
     * @param entityId   unique entity identifier
     * @param name       display name
     * @param entityType underlying a Minecraft entity type
     */
    protected SpawnableEntity(@NonNull String entityId, @NonNull String name, @NonNull EntityType entityType) {
        super(entityId, name, entityType);
    }

    /**
     * Configures spawning rules, probability, mode, and affixes using a fluent builder consumer.
     *
     * @param consumer action configuring the spawning builder
     */
    public void spawning(@NonNull Consumer<SpawningBuilder> consumer) {
        Objects.requireNonNull(consumer, "consumer cannot be null");
        SpawningBuilder builder = new SpawningBuilder();
        builder.probability(this.spawnProbability);
        builder.mode(this.spawnMode);
        builder.affixes(this.affixChance, this.maxAffixes);
        consumer.accept(builder);
        this.spawnProbability = builder.getProbability();
        this.spawnMode = builder.getMode();
        this.affixChance = builder.getAffixChance();
        this.maxAffixes = builder.getMaxAffixes();
        this.spawnProfile = builder.buildProfile();
    }

    /**
     * Sets elite affix rolling chance.
     *
     * @param affixChance probability in [0.0, 1.0]
     * @return this entity
     */
    public SpawnableEntity affixChance(double affixChance) {
        if (affixChance < 0.0 || affixChance > 1.0) {
            throw new IllegalArgumentException("affixChance must be in [0.0, 1.0], got: " + affixChance);
        }
        this.affixChance = affixChance;
        return this;
    }

    /**
     * Sets maximum number of rollable elite affixes.
     *
     * @param maxAffixes maximum affixes
     * @return this entity
     */
    public SpawnableEntity maxAffixes(int maxAffixes) {
        this.maxAffixes = Math.max(1, maxAffixes);
        return this;
    }

    /**
     * Evaluates whether this entity can spawn at the specified location and world.
     *
     * @param location location to evaluate
     * @param world    world to evaluate
     * @return {@code true} if all spawn profile conditions are satisfied, {@code false} otherwise
     */
    public boolean canSpawnAt(@Nullable Location location, @Nullable World world) {
        return spawnProfile.canSpawn(location, world);
    }

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
                        .set(getAffixesKey(), PersistentDataType.STRING, String.join(",", affixIds));
            }
        }

        return spawned;
    }

    @Override
    protected void onRehydrate(LivingEntity existing) {
        super.onRehydrate(existing);

        String persistentAffixes = existing.getPersistentDataContainer().get(getAffixesKey(), PersistentDataType.STRING);
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
