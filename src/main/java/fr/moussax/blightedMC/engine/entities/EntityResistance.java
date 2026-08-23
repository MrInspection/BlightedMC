package fr.moussax.blightedMC.engine.entities;

import fr.moussax.blightedMC.engine.entities.immunity.DamageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
