package fr.moussax.blightedMC.utils.sound;

import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.Objects;

/**
 * Represents a configurable sound effect in BlightedMC.
 *
 * <p>Each sound has a type, volume, pitch, and optional delay before playing.
 * Provides a method to play the sound at a specific {@link Location} using
 * {@link fr.moussax.blightedMC.utils.Utilities#delay(Runnable, long)}.</p>
 *
 * @param sound  the Bukkit {@link org.bukkit.Sound} type
 * @param volume the volume of the sound
 * @param pitch  the pitch of the sound
 * @param delay  the delay in ticks before the sound is played
 */
public record BlightedSound(Sound sound, float volume, float pitch, long delay) {

    /**
     * Plays this sound at the specified location after the configured delay.
     *
     * @param location the location where the sound should be played
     */
    public void play(Location location) {
        Utilities.delay(() -> Objects.requireNonNull(location.getWorld())
            .playSound(location, sound, volume, pitch), delay);
    }
}
