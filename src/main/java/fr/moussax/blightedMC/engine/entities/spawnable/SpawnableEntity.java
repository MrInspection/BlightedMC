package fr.moussax.blightedMC.engine.entities.spawnable;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.affixes.AffixRegistry;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import fr.moussax.blightedMC.engine.entities.spawnable.condition.SpawnCondition;
import fr.moussax.blightedMC.engine.entities.spawnable.engine.SpawnMode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public abstract class SpawnableEntity extends BlightedEntity {
    public static final NamespacedKey AFFIXES_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_active_affix");

    @Getter
    private final double spawnProbability;
    @Getter
    private final SpawnMode spawnMode;
    private SpawnProfile spawnProfile;

    @Getter @Setter
    private double affixChance = 0.0;

    protected SpawnableEntity(String entityId, String name, int maxHealth, EntityType entityType, double probability) {
        this(entityId, name, maxHealth, 1, 0, entityType, probability, SpawnMode.REPLACEMENT);
    }

    protected SpawnableEntity(String entityId, String name, int maxHealth, EntityType entityType, double probability, SpawnMode mode) {
        this(entityId, name, maxHealth, 1, 0, entityType, probability, mode);
    }

    protected SpawnableEntity(String entityId, String name, int maxHealth, int damage, EntityType entityType, double probability, SpawnMode mode) {
        this(entityId, name, maxHealth, damage, 0, entityType, probability, mode);
    }

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

    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity spawned = super.spawn(location);

        if (affixChance > 0.0 && Math.random() <= affixChance) {
            EntityComponent affix = AffixRegistry.getRandomAffix();
            if (affix != null) {
                addComponent(affix);
                spawned.getPersistentDataContainer().set(AFFIXES_KEY, PersistentDataType.STRING, affix.getId());

                spawned.setCustomName("§d§l" + name);
                spawned.setCustomNameVisible(true);

                startEliteAura();
            }
        }

        return spawned;
    }

    @Override
    protected void onRehydrate(LivingEntity existing) {
        super.onRehydrate(existing);

        String affixId = existing.getPersistentDataContainer().get(AFFIXES_KEY, PersistentDataType.STRING);
        if (affixId != null) {
            EntityComponent affix = AffixRegistry.getAffixById(affixId);
            if (affix != null) {
                addComponent(affix);
                startEliteAura();
            }
        }
    }

    private void startEliteAura() {
        addCoreAbility(5L, 15L, () -> {
            if (isNotAlive()) return;

            Location center = entity.getLocation().add(0, entity.getHeight() / 2.0, 0);
            entity.getWorld().spawnParticle(Particle.ENCHANT, center, 10, 0.5, 0.5, 0.5, 0.1);
            entity.getWorld().spawnParticle(Particle.WITCH, center, 2, 0.4, 0.4, 0.4, 0.05);
        });
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
