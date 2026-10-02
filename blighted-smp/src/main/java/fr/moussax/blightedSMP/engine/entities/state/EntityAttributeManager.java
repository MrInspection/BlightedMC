package fr.moussax.blightedSMP.engine.entities.state;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manages custom attribute configuration, modifier stripping, health clamping,
 * and live entity attribute synchronization.
 */
public final class EntityAttributeManager {

    private final Map<Attribute, Double> attributes;

    public EntityAttributeManager() {
        this(new HashMap<>());
    }

    private EntityAttributeManager(@NonNull Map<Attribute, Double> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    /**
     * Sets an attribute base value. If the entity is currently active, strips all
     * existing modifiers and updates the live Bukkit AttributeInstance immediately.
     *
     * @param attribute  target attribute
     * @param value      base attribute value
     * @param liveEntity live entity, or null if unspawned
     */
    public void setAttribute(@NonNull Attribute attribute, double value, @Nullable LivingEntity liveEntity) {
        Objects.requireNonNull(attribute, "attribute cannot be null");
        attributes.put(attribute, value);
        if (liveEntity != null) {
            applyAttributeToLiveEntity(attribute, value, liveEntity);
        }
    }

    /**
     * Returns the effective base value for an attribute, querying the live entity if active,
     * or the configured attribute map if unspawned.
     *
     * @param attribute  target attribute
     * @param liveEntity live entity, or null if unspawned
     * @return effective base value, or 0.0 if not configured
     */
    public double getAttributeValue(@NonNull Attribute attribute, @Nullable LivingEntity liveEntity) {
        Objects.requireNonNull(attribute, "attribute cannot be null");
        if (liveEntity != null && liveEntity.isValid() && !liveEntity.isDead()) {
            AttributeInstance instance = liveEntity.getAttribute(attribute);
            if (instance != null) {
                return instance.getValue();
            }
        }
        return attributes.getOrDefault(attribute, 0.0);
    }

    /**
     * Applies combat stats and all configured attributes to a live entity on initial spawn.
     * Sets health to max health and locks persistence properties.
     *
     * @param liveEntity live entity
     * @param maxHealth  maximum health
     * @param damage     attack damage
     * @param defense    armor defense
     * @param isBoss     whether entity is a boss
     */
    public void initialize(
            @NonNull LivingEntity liveEntity,
            int maxHealth,
            int damage,
            int defense,
            boolean isBoss
    ) {
        applyBaseAttributes(liveEntity, maxHealth, damage, defense);

        AttributeInstance maxHealthAttribute = liveEntity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            liveEntity.setHealth(maxHealthAttribute.getValue());
        }

        lockEntityProperties(liveEntity, isBoss);
    }

    /**
     * Rehydrates combat stats and attributes when binding to an existing world entity.
     * Clamps health to current max health and locks persistence properties.
     *
     * @param liveEntity live entity
     * @param maxHealth  maximum health
     * @param damage     attack damage
     * @param defense    armor defense
     * @param isBoss     whether entity is a boss
     */
    public void rehydrate(
            @NonNull LivingEntity liveEntity,
            int maxHealth,
            int damage,
            int defense,
            boolean isBoss
    ) {
        applyBaseAttributes(liveEntity, maxHealth, damage, defense);

        AttributeInstance maxHealthAttribute = liveEntity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            liveEntity.setHealth(Math.min(liveEntity.getHealth(), maxHealthAttribute.getValue()));
        }

        lockEntityProperties(liveEntity, isBoss);
    }

    /**
     * Sets the live entity's current health, clamped between 0 and maximum health.
     *
     * @param liveEntity        live entity
     * @param health            target health value
     * @param fallbackMaxHealth fallback maximum health
     */
    public void setHealth(@Nullable LivingEntity liveEntity, double health, double fallbackMaxHealth) {
        if (liveEntity != null && liveEntity.isValid() && !liveEntity.isDead()) {
            AttributeInstance maxHealthAttribute = liveEntity.getAttribute(Attribute.MAX_HEALTH);
            double max = maxHealthAttribute != null ? maxHealthAttribute.getValue() : fallbackMaxHealth;
            liveEntity.setHealth(Math.max(0.0, Math.min(health, max)));
        }
    }

    /**
     * Returns the live entity's current health, or {@code 0.0} if unspawned or dead.
     *
     * @param liveEntity live entity
     * @return current health
     */
    public double getHealth(@Nullable LivingEntity liveEntity) {
        return liveEntity != null && liveEntity.isValid() && !liveEntity.isDead() ? liveEntity.getHealth() : 0.0;
    }

    /**
     * Applies standard Minecraft persistence and despawn settings based on boss status.
     *
     * @param liveEntity target entity
     * @param isBoss     whether the entity is a boss
     */
    public void lockEntityProperties(@NonNull LivingEntity liveEntity, boolean isBoss) {
        liveEntity.setRemoveWhenFarAway(!isBoss);
        liveEntity.setPersistent(isBoss);
        liveEntity.setCanPickupItems(false);
    }

    private void applyBaseAttributes(@NonNull LivingEntity liveEntity, int maxHealth, int damage, int defense) {
        if (maxHealth > 0) {
            attributes.put(Attribute.MAX_HEALTH, (double) maxHealth);
        }
        if (damage > 0) {
            attributes.put(Attribute.ATTACK_DAMAGE, (double) damage);
        }
        if (defense > 0) {
            attributes.put(Attribute.ARMOR, (double) defense);
        }

        for (Map.Entry<Attribute, Double> entry : new HashMap<>(attributes).entrySet()) {
            applyAttributeToLiveEntity(entry.getKey(), entry.getValue(), liveEntity);
        }
    }

    private void applyAttributeToLiveEntity(@NonNull Attribute attribute, double value, @NonNull LivingEntity liveEntity) {
        if (!liveEntity.isValid() || liveEntity.isDead()) {
            return;
        }
        AttributeInstance instance = liveEntity.getAttribute(attribute);
        if (instance != null) {
            for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
                instance.removeModifier(modifier);
            }
            instance.setBaseValue(value);
        }
    }

    /**
     * Creates a deep copy of this attributes manager.
     *
     * @return detached copy
     */
    @NonNull
    public EntityAttributeManager copy() {
        return new EntityAttributeManager(this.attributes);
    }
}
