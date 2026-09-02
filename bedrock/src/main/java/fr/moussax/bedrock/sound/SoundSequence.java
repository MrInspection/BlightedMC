package fr.moussax.bedrock.sound;

import org.bukkit.Location;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sequence of {@link SoundCue} objects to be played in order.
 *
 * <p>Each sequence can be used to create complex audio effects in the game,
 * such as boss spawn sounds, item pickups, or forging effects.</p>
 *
 * <p>Sounds in the sequence are played in the order they appear using
 * {@link #play(Location)}.</p>
 */
public record SoundSequence(List<SoundCue> cues) {

    /**
     * Creates a new sound sequence from a list of {@link SoundCue} objects.
     *
     * @param cues list of sound cues, copied to ensure immutability
     */
    public SoundSequence(List<SoundCue> cues) {
        this.cues = List.copyOf(Objects.requireNonNull(cues));
    }

    /**
     * Creates a new sound sequence from a varargs array of {@link SoundCue} objects.
     *
     * @param cues sound cues to include in the sequence
     */
    public SoundSequence(@NonNull SoundCue... cues) {
        this(Arrays.asList(cues));
    }

    /**
     * Plays all sounds in this sequence at the given location in order.
     *
     * @param location the location where the sounds will be played
     */
    public void play(Location location) {
        for (SoundCue cue : cues) {
            cue.play(location);
        }
    }
}
