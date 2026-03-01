package fr.moussax.blightedMC.smp.core.entities.attachment;

/**
 * Defines the lifecycle relationship between an {@link EntityAttachment} and its owner.
 *
 * <ul>
 *   <li>{@link #BODY} — the attachment is a structural part of the owner. If the attachment
 *       dies, the owner dies. Damage to the owner is forwarded to this attachment so health
 *       stays in sync. Direct hits on the attachment are redirected to the owner.</li>
 *   <li>{@link #DEPENDENT} — the attachment is a subordinate of the owner. It dies when the
 *       owner dies, but its death does not kill the owner. It does not mirror owner health.</li>
 * </ul>
 */
public enum AttachmentRole {
    BODY,
    DEPENDENT
}
