package fr.moussax.blightedMC.core.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines immunity attributes for an entity class.
 * <br />The entity will be immune to the specified damage types.
 * <p>
 * Example:
 * <pre>{@code
 * EntityAttributes({Attributes.MELEE_IMMUNITY, Attributes.FIRE_IMMUNITY})
 *  public class FireGolem extends BlightedEntity { ... }
 *
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityAttributes {
  Attributes[] value();

  enum Attributes {
    MELEE_IMMUNITY,
    PROJECTILE_IMMUNITY,
    FIRE_IMMUNITY,
    MAGIC_IMMUNITY,
  }
}
