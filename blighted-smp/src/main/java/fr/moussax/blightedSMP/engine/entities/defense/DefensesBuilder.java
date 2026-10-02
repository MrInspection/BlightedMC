package fr.moussax.blightedSMP.engine.entities.defense;

import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import org.jspecify.annotations.NonNull;

/**
 * Fluent builder for configuring mob damage immunities and resistances.
 */
public final class DefensesBuilder {

    private final BlightedEntity entity;

    public DefensesBuilder(@NonNull BlightedEntity entity) {
        this.entity = entity;
    }

    /**
     * Registers total damage immunity against the specified damage types.
     *
     * @param types damage types to completely negate
     * @return this builder
     */
    public DefensesBuilder immune(@NonNull DamageType... types) {
        for (DamageType type : types) {
            entity.addImmunity(type);
        }
        return this;
    }

    /**
     * Registers proportional damage reduction against the specified damage type.
     *
     * @param type    damage type to resist
     * @param percent percentage of damage resisted in range [0.0, 100.0] (e.g. 50.0 for 50% reduction)
     * @return this builder
     */
    public DefensesBuilder resist(@NonNull DamageType type, double percent) {
        entity.addResistance(type, percent);
        return this;
    }
}
