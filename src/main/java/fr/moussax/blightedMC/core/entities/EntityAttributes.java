package fr.moussax.blightedMC.core.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
