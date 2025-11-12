package fr.moussax.blightedMC.core.entities;

import org.bukkit.entity.Entity;

/**
 * Represents an attachment relationship between a Minecraft entity and its owning {@link BlightedEntity}.
 * <p>
 * This record is used to track entities that are logically tied to a parent entity,
 * such as minions, summoned creatures, or auxiliary objects. Attachments are automatically
 * managed by the owning entity for lifecycle events like spawn and death.
 *
 * @param entity the Bukkit {@link Entity} that is attached to the owner
 * @param owner  the {@link BlightedEntity} that owns or controls the attached entity
 */
public record EntityAttachment(Entity entity, BlightedEntity owner) { }
