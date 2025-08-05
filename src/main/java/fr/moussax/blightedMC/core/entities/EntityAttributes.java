package fr.moussax.blightedMC.core.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define various attributes and immunities for an entity class.
 * <p>
 * Can specify one or more {@link Attributes} such as immunities to melee, projectiles, fire, or magic.
 * Also supports a nested {@link MagicResistance} annotation to define a specific magic resistance value.
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

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface MagicResistance {
    double value();
  }
}
