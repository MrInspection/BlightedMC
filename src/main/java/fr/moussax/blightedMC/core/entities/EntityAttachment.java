package fr.moussax.blightedMC.core.entities;

import org.bukkit.entity.Entity;

/**
 * Represents a managed attachment between a Minecraft {@link Entity} and its owning {@link AbstractBlightedEntity}.
 * <p>
 * Attachments are entities logically tied to a parent entity, such as minions, summoned creatures,
 * or auxiliary objects. The owning entity automatically manages attachments for lifecycle events
 * like spawning, death, and removal.
 *
 * @param entity the attached Bukkit {@link Entity}
 * @param owner  the {@link AbstractBlightedEntity} that controls this attachment
 */
public record EntityAttachment(Entity entity, AbstractBlightedEntity owner) {
}
