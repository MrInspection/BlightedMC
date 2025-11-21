package fr.moussax.blightedMC.utils.sound;

import fr.moussax.blightedMC.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.Objects;

public record BlightedSound(Sound sound, float volume, float pitch, long delay) {
    public void play(Location location) {
        Utilities.delay(() -> Objects.requireNonNull(location.getWorld()).playSound(location, sound, volume, pitch), delay);
    }
}
