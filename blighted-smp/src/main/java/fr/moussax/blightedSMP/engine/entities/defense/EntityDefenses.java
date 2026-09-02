package fr.moussax.blightedSMP.engine.entities.defense;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates damage immunities and type resistance rules for an entity.
 */
public final class EntityDefenses {

    private final List<EntityImmunity> immunities = new ArrayList<>();
    private final Map<DamageType, Double> resistances = new EnumMap<>(DamageType.class);

    /**
     * Constructs an empty defense profile.
     */
    public EntityDefenses() {
    }

    /**
     * Creates a defensive copy of this defense profile.
     *
     * @return a new EntityDefenses instance with identical rules
     */
    public EntityDefenses copy() {
        EntityDefenses copy = new EntityDefenses();
        copy.immunities.addAll(this.immunities);
        copy.resistances.putAll(this.resistances);
        return copy;
    }

    /**
     * Creates an {@code EntityDefenses} profile by reading {@link EntityImmunities} and
     * {@link EntityResistance}/{@link EntityResistances} annotations declared on an entity class.
     *
     * @param entityClass entity class to reflectively inspect
     * @return parsed defense profile
     */
    public static EntityDefenses fromClass(@NonNull Class<?> entityClass) {
        EntityDefenses defenses = new EntityDefenses();

        EntityImmunities immunitiesAnnotation = entityClass.getAnnotation(EntityImmunities.class);
        if (immunitiesAnnotation != null) {
            Collections.addAll(defenses.immunities, immunitiesAnnotation.value());
        }

        EntityResistances resistancesContainer = entityClass.getAnnotation(EntityResistances.class);
        if (resistancesContainer != null) {
            for (EntityResistance rule : resistancesContainer.value()) {
                defenses.resistances.put(rule.type(), rule.percent());
            }
        }
        EntityResistance singleResistance = entityClass.getAnnotation(EntityResistance.class);
        if (singleResistance != null) {
            defenses.resistances.put(singleResistance.type(), singleResistance.percent());
        }

        return defenses;
    }

    /**
     * Adds a dynamic immunity rule.
     *
     * @param immunity immunity rule to add
     */
    public void addImmunity(@NonNull EntityImmunity immunity) {
        this.immunities.add(immunity);
    }

    /**
     * Adds a dynamic damage resistance percentage rule.
     *
     * @param type    damage type
     * @param percent percentage resisted (0.0 to 100.0)
     */
    public void addResistance(@NonNull DamageType type, double percent) {
        this.resistances.put(type, percent);
    }

    /**
     * Evaluates active immunity rules against a damage event.
     *
     * @param target entity receiving damage
     * @param event  damage event
     * @return matching immunity rule, or {@code null} if none applies
     */
    @Nullable
    public EntityImmunity getTriggeredImmunity(LivingEntity target, EntityDamageEvent event) {
        if (immunities.isEmpty()) {
            return null;
        }
        for (EntityImmunity rule : immunities) {
            if (rule.isImmune(target, event)) {
                return rule;
            }
        }
        return null;
    }

    /**
     * Evaluates active resistance rules and returns the highest resistance percentage.
     *
     * @param target entity receiving damage
     * @param event  damage event
     * @return highest resistance percentage (0.0 if no resistance applies)
     */
    public double getResistancePercent(LivingEntity target, EntityDamageEvent event) {
        if (resistances.isEmpty()) {
            return 0.0;
        }
        double highestResistance = 0.0;
        for (Map.Entry<DamageType, Double> entry : resistances.entrySet()) {
            DamageType type = entry.getKey();
            if (type.isImmune(target, event)) {
                highestResistance = Math.max(highestResistance, entry.getValue());
            }
        }
        return highestResistance;
    }

    /**
     * Returns an unmodifiable list of active immunity rules.
     *
     * @return active immunity rules
     */
    public List<EntityImmunity> getImmunities() {
        return Collections.unmodifiableList(immunities);
    }

    /**
     * Returns an unmodifiable map of active damage resistance percentages.
     *
     * @return active resistance map
     */
    public Map<DamageType, Double> getResistances() {
        return Collections.unmodifiableMap(resistances);
    }
}
