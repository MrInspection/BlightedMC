package fr.moussax.blightedMC.engine.entities;

import fr.moussax.blightedMC.engine.entities.immunity.DamageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the damage types to which an entity is immune.
 *
 * <p>This annotation is applied to entity classes whose damage handling
 * should ignore the specified {@link DamageType}s.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityImmunities {

    /**
     * Returns the damage types to which the annotated entity is immune.
     *
     * @return the entity's damage immunities
     */
    DamageType[] value();
}
