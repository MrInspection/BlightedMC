package fr.moussax.blightedSMP.engine.entities.state;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Fluent builder for configuring mob attributes with typed convenience methods.
 */
public final class EntityAttributesBuilder {

    private final Map<Attribute, Double> attributes = new HashMap<>();

    /**
     * Sets an arbitrary Minecraft attribute value.
     *
     * @param attribute target attribute
     * @param value     base attribute value
     * @return this builder
     */
    public EntityAttributesBuilder set(@NonNull Attribute attribute, double value) {
        this.attributes.put(Objects.requireNonNull(attribute, "attribute cannot be null"), value);
        return this;
    }

    /**
     * Sets maximum health.
     *
     * @param maxHealth maximum health
     * @return this builder
     */
    public EntityAttributesBuilder maxHealth(double maxHealth) {
        return set(Attribute.MAX_HEALTH, maxHealth);
    }

    /**
     * Sets attack damage.
     *
     * @param damage attack damage
     * @return this builder
     */
    public EntityAttributesBuilder attackDamage(double damage) {
        return set(Attribute.ATTACK_DAMAGE, damage);
    }

    /**
     * Sets base armor rating.
     *
     * @param armor armor value
     * @return this builder
     */
    public EntityAttributesBuilder armor(double armor) {
        return set(Attribute.ARMOR, armor);
    }

    /**
     * Sets base armor toughness rating.
     *
     * @param toughness armor toughness value
     * @return this builder
     */
    public EntityAttributesBuilder armorToughness(double toughness) {
        return set(Attribute.ARMOR_TOUGHNESS, toughness);
    }

    /**
     * Sets movement speed.
     *
     * @param speed movement speed (vanilla default for zombies is ~0.23)
     * @return this builder
     */
    public EntityAttributesBuilder movementSpeed(double speed) {
        return set(Attribute.MOVEMENT_SPEED, speed);
    }

    /**
     * Sets knockback resistance ratio (0.0 to 1.0).
     *
     * @param resistance knockback resistance
     * @return this builder
     */
    public EntityAttributesBuilder knockbackResistance(double resistance) {
        return set(Attribute.KNOCKBACK_RESISTANCE, resistance);
    }

    /**
     * Sets entity scale multiplier (1.0 is default size).
     *
     * @param scale scale multiplier
     * @return this builder
     */
    public EntityAttributesBuilder scale(double scale) {
        return set(Attribute.SCALE, scale);
    }

    /**
     * Sets player follow range in blocks.
     *
     * @param range target follow range
     * @return this builder
     */
    public EntityAttributesBuilder followRange(double range) {
        return set(Attribute.FOLLOW_RANGE, range);
    }

    /**
     * Sets attack speed multiplier.
     *
     * @param attackSpeed attack speed
     * @return this builder
     */
    public EntityAttributesBuilder attackSpeed(double attackSpeed) {
        return set(Attribute.ATTACK_SPEED, attackSpeed);
    }

    /**
     * Sets flying speed multiplier.
     *
     * @param flyingSpeed flying speed
     * @return this builder
     */
    public EntityAttributesBuilder flyingSpeed(double flyingSpeed) {
        return set(Attribute.FLYING_SPEED, flyingSpeed);
    }

    /**
     * Sets step height in blocks (e.g. 1.0 allows stepping up 1 full block without jumping).
     *
     * @param stepHeight step height in blocks
     * @return this builder
     */
    public EntityAttributesBuilder stepHeight(double stepHeight) {
        return set(Attribute.STEP_HEIGHT, stepHeight);
    }

    /**
     * Sets safe fall distance in blocks before taking fall damage.
     *
     * @param distance safe fall distance
     * @return this builder
     */
    public EntityAttributesBuilder safeFallDistance(double distance) {
        return set(Attribute.SAFE_FALL_DISTANCE, distance);
    }

    /**
     * Sets fall damage multiplier.
     *
     * @param multiplier fall damage multiplier (0.0 disables fall damage)
     * @return this builder
     */
    public EntityAttributesBuilder fallDamageMultiplier(double multiplier) {
        return set(Attribute.FALL_DAMAGE_MULTIPLIER, multiplier);
    }

    /**
     * Returns an unmodifiable snapshot of configured attributes.
     *
     * @return map of attributes
     */
    @NonNull
    public Map<Attribute, Double> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Applies all attributes from this builder to the given manager and live entity.
     *
     * @param manager    attribute manager
     * @param liveEntity live entity, or null if unspawned
     */
    public void applyTo(@NonNull EntityAttributeManager manager, @Nullable LivingEntity liveEntity) {
        Objects.requireNonNull(manager, "manager cannot be null");
        attributes.forEach((attribute, value) -> manager.setAttribute(attribute, value, liveEntity));
    }
}
