package fr.moussax.blightedMC.utils.sound;

import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.*;

public record SoundSequence(List<BlightedSound> sounds) {
  public SoundSequence(List<BlightedSound> sounds) {
    this.sounds = List.copyOf(Objects.requireNonNull(sounds));
  }

  public SoundSequence(BlightedSound... sounds) {
    this(Arrays.asList(sounds));
  }

  public void play(Location location) {
    for (BlightedSound sound : sounds) {
      sound.play(location);
    }
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

  public static final SoundSequence XP_PICKUP = new SoundSequence(
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f, 0),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.6f, 2),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.7f, 4),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.8f, 6),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.9f, 8),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.0f, 10),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.1f, 12),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f, 14),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.3f, 16),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.4f, 18)
  );
}
