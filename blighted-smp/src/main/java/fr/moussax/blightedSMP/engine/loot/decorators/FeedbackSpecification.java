package fr.moussax.blightedSMP.engine.loot.decorators;

import org.bukkit.Sound;

/**
 * Encapsulates feedback properties (custom message, message prefix, sound, pitch) for loot decoration.
 *
 * @param customMessage explicit message sent directly to player, or {@code null}
 * @param messagePrefix prefix prepended to display name, or {@code null}
 * @param sound         sound played on drop execution, or {@code null}
 * @param pitch         pitch multiplier for sound playback
 */
public record FeedbackSpecification(String customMessage, String messagePrefix, Sound sound, float pitch) {

    /**
     * Creates a specification with a display name prefix and sound feedback.
     *
     * @param messagePrefix display name prefix
     * @param sound         sound to play
     * @param pitch         sound pitch multiplier
     * @return new feedback specification
     */
    public static FeedbackSpecification full(String messagePrefix, Sound sound, float pitch) {
        return new FeedbackSpecification(null, messagePrefix, sound, pitch);
    }

    /**
     * Creates a specification with sound feedback only.
     *
     * @param sound sound to play
     * @param pitch sound pitch multiplier
     * @return new feedback specification
     */
    public static FeedbackSpecification soundOnly(Sound sound, float pitch) {
        return new FeedbackSpecification(null, null, sound, pitch);
    }

    /**
     * Creates a specification with a custom message only.
     *
     * @param customMessage message to send
     * @return new feedback specification
     */
    public static FeedbackSpecification customMessage(String customMessage) {
        return new FeedbackSpecification(customMessage, null, null, 1.0f);
    }
}
