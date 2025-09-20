package fr.moussax.blightedMC.utils.sound;

import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class SoundSequence {
  private final List<BlightedSound> sounds;

  public SoundSequence(List<BlightedSound> sounds) {
    this.sounds = Collections.unmodifiableList(Objects.requireNonNull(sounds));
  }

  public SoundSequence(BlightedSound... sounds) {
    this(Arrays.asList(sounds));
  }

  public void play(Location location) {
    for (BlightedSound sound : sounds) {
      sound.play(location);
    }
  }

  public List<BlightedSound> getSounds() {
    return sounds;
  }

  public static final SoundSequence BOSS_SPAWN = new SoundSequence(
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 9f, 1),
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 9f, 4),
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 5f, 7),
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 5f, 10),
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 5f, 13),
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 5f, 16),
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 1f, 19),
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 1f, 22),
    new BlightedSound(Sound.ENTITY_WITHER_SHOOT, 1f, 1f, 25),
    new BlightedSound(Sound.ENTITY_WITHER_SPAWN, 1f, -25f, 28),
    new BlightedSound(Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f, 28)
  );
}
