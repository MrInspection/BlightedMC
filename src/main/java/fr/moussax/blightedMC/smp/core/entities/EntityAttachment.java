package fr.moussax.blightedMC.smp.core.entities;

import org.bukkit.entity.Entity;

/**
 * Defines a managed association between a Bukkit {@link Entity} and its owning
 * {@link AbstractBlightedEntity}.
 * <p>
 * An {@code EntityAttachment} represents entities that exist as an extension of a parent
 * blighted entity, such as summoned units, minions, or auxiliary constructs. The owner
 * is responsible for managing the attachment’s lifecycle, including creation,
 * synchronization, and cleanup on death or despawn.
 *
 * @param entity the attached Bukkit entity instance
 * @param owner  the blighted entity that owns and manages this attachment
 */
public record EntityAttachment(Entity entity, AbstractBlightedEntity owner) {
}
