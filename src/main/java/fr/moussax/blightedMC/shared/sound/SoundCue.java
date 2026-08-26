package fr.moussax.blightedMC.shared.sound;

import fr.moussax.blightedMC.shared.scheduling.PluginContext;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a configurable sound effect in BlightedMC.
 *
 * <p>Each sound has a type, volume, pitch, and optional delay before playing.
 * Provides a method to play the sound at a specific {@link Location} using
 * {@link PluginContext#delay(Runnable, long)}.</p>
 *
 * @param sound  the Bukkit {@link org.bukkit.Sound} type
 * @param volume the volume of the sound
 * @param pitch  the pitch of the sound
 * @param delay  the delay in ticks before the sound is played
 */
public record SoundCue(Sound sound, float volume, float pitch, long delay) {

    /**
     * Plays this sound at the specified location after the configured delay.
     *
     * @param location the location where the sound should be played
     */
    public void play(@NonNull Location location) {
        PluginContext.delay(() -> Objects.requireNonNull(location.getWorld())
            .playSound(location, sound, volume, pitch), delay);
    }
}
