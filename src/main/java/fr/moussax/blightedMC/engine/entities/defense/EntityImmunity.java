package fr.moussax.blightedMC.engine.entities.defense;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Represents a rule that determines if an entity is immune to a specific type of damage.
 *
 * <p>Implementations of this interface define conditions under which a {@link LivingEntity}
 * should ignore a given {@link EntityDamageEvent}.</p>
 */
@FunctionalInterface
public interface EntityImmunity {

    /**
     * Determines whether the specified entity is immune to the given damage event.
     *
     * @param livingEntity the entity that may be affected by the event
     * @param event        the damage event to check
     * @return {@code true} if the entity is immune to this damage event, {@code false} otherwise
     */
    boolean isImmune(LivingEntity livingEntity, EntityDamageEvent event);

    /**
     * Gets the formatted immunity warning message sent to players when damage is prevented.
     *
     * @return immunity notification message
     */
    default String getImmunityMessage() {
        return "§c This creature is immune to this type of damage!";
    }
}
