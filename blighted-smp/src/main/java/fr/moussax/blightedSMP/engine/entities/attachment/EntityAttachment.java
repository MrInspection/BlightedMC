package fr.moussax.blightedSMP.engine.entities.attachment;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Associates a Bukkit {@link Entity} attachment with its role, local translation offset,
 * and rotation synchronization flags relative to a parent {@link fr.moussax.blightedSMP.engine.entities.BlightedEntity}.
 *
 * @param entity      the attached Bukkit entity
 * @param role        the functional role this attachment plays (VISUAL, HITBOX, or SUBORDINATE)
 * @param localOffset relative (x, y, z) translation offset relative to the base entity
 * @param syncYaw     whether horizontal rotation follows the base entity facing yaw
 * @param syncPitch   whether vertical pitch follows the base entity head pitch
 */
public record EntityAttachment(
        Entity entity,
        AttachmentRole role,
        Vector localOffset,
        boolean syncYaw,
        boolean syncPitch
) {
    public EntityAttachment(Entity entity, AttachmentRole role) {
        this(entity, role, new Vector(0, 0, 0), true, false);
    }

    public EntityAttachment(Entity entity, AttachmentRole role, Vector localOffset) {
        this(entity, role, localOffset != null ? localOffset : new Vector(0, 0, 0), true, false);
    }
}
