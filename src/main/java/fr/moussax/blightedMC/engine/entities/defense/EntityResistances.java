package fr.moussax.blightedMC.engine.entities.defense;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares damage type resistances for an entity.
 *
 * <p>This annotation is applied to entity classes whose damage handling
 * should reduce damage from specified {@link EntityResistance} rules.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityResistances {

    /**
     * Returns the damage resistance rules for the annotated entity.
     *
     * @return the entity's damage resistances
     */
    EntityResistance[] value();
}
