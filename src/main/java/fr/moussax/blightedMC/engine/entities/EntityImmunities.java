package fr.moussax.blightedMC.engine.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityImmunities {
    ImmunityType[] value();

    enum ImmunityType {
        MELEE,
        PROJECTILE,
        FIRE,
        MACE
    }
}
