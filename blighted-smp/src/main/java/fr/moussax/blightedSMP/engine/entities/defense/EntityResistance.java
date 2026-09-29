package fr.moussax.blightedSMP.engine.entities.defense;

import java.lang.annotation.*;

/**
 * Declares a single damage type resistance percentage for an entity.
 */
@Repeatable(EntityResistances.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityResistance {

    /**
     * The damage type to resist.
     *
     * @return damage type
     */
    DamageType type();

    /**
     * Percentage of incoming damage resisted (e.g. 50.0 for 50% damage reduction).
     *
     * @return percentage of damage resisted
     */
    double percent();
}
