package fr.moussax.blightedMC.smp.core.entities.immunity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Represents a rule that determines if an entity is immune to a specific type of damage.
 * <p>
 * Implementations of this interface define conditions under which a {@link LivingEntity}
 * should ignore a given {@link EntityDamageEvent}.
 */
@FunctionalInterface
public interface EntityImmunity {
    EntityImmunity FIRE = new FireImmunity();
    EntityImmunity MELEE = new MeleeImmunity();
    EntityImmunity PROJECTILE = new ProjectileImmunity();
    EntityImmunity MACE = new MaceImmunity();

    /**
     * Determines whether the specified entity is immune to the given damage event.
     *
     * @param livingEntity the entity that may be affected by the event
     * @param event        the damage event to check
     * @return {@code true} if the entity is immune to this damage event, {@code false} otherwise
     */
    boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event);

    /**
     * @return The message sent to the player when this immunity blocks damage.
     */
    default String getImmunityMessage() {
        return "§4 ■ §cThis creature is immune to this type of damage!";
    }
}
