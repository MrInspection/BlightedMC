package fr.moussax.blightedMC.core.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the damage immunities for a {@link BlightedEntity}.
 *
 * <p>Entities annotated with this annotation will automatically be immune
 * to the specified damage types. Immunity rules are initialized during
 * entity spawning and attachment.
 *
 * <p><b>Usage Example:</b></p>
 *
 * <pre>{@code
 * @EntityImmunities(ImmunityType.PROJECTILE)
 * public class ArmoredKnight extends BlightedEntity {
 *     // Immune to projectile damage
 * }
 * }</pre>
 *
 * @see BlightedEntity#initImmunityRules()
 * @see ImmunityType
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityImmunities {
    ImmunityType[] value();

    enum ImmunityType {
        MELEE,
        PROJECTILE,
        FIRE,
        MAGIC,
    }
}
