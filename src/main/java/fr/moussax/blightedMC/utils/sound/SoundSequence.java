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

  public static final SoundSequence BLIGHTED_GEMSTONE_CONSUME = new SoundSequence(
    new BlightedSound(Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0F, 0.0F, 0L),
    new BlightedSound(Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 0.1F, 2L),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.2F, 4L),
    new BlightedSound(Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0F, 0.3F, 6L),
    new BlightedSound(Sound.ENTITY_EVOKER_CAST_SPELL, 1.0F, 0.4F, 8L),
    new BlightedSound(Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 0.5F, 10L),
    new BlightedSound(Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0F, 0.6F, 12L),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.7F, 14L),
    new BlightedSound(Sound.ENTITY_EVOKER_CAST_SPELL, 1.0F, 0.8F, 16L),
    new BlightedSound(Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 0.9F, 18L),
    new BlightedSound(Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0F, 1.0F, 20L),
    new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.1F, 22L)
  );
}
