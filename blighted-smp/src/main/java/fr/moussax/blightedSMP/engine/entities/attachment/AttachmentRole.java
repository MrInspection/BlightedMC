package fr.moussax.blightedSMP.engine.entities.attachment;

/**
 * Defines the functional role of an attached entity in a composite {@link fr.moussax.blightedSMP.engine.entities.BlightedEntity}.
 *
 * <ul>
 *   <li>{@link #VISUAL} — A non-interactive render element (Display entity: Item, Block, or Text)
 *       locked to a local 3D offset relative to the base entity's origin and facing yaw.</li>
 *   <li>{@link #HITBOX} — An {@link org.bukkit.entity.Interaction} entity that intercepts melee/projectile attacks,
 *       redirects damage to the base entity, and forwards damage events.</li>
 *   <li>{@link #SUBORDINATE} — A companion or non-rigid attached entity that cleans up on owner death.</li>
 * </ul>
 */
public enum AttachmentRole {
    VISUAL,
    HITBOX,
    SUBORDINATE
}
