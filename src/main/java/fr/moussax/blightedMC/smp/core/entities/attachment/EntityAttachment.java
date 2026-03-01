package fr.moussax.blightedMC.smp.core.entities.attachment;

import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import org.bukkit.entity.Entity;

/**
 * Associates a Bukkit {@link Entity} with its role in a parent {@link AbstractBlightedEntity}.
 *
 * <p>The {@link AttachmentRole} determines the lifecycle relationship:
 * {@link AttachmentRole#BODY} attachments are structural and their death triggers the owner's death,
 * while {@link AttachmentRole#DEPENDENT} attachments are subordinates that die with the owner
 * but do not control it.</p>
 *
 * @param entity the attached Bukkit entity
 * @param role   the lifecycle role this attachment plays relative to its owner
 */
public record EntityAttachment(Entity entity, AttachmentRole role) {
}
