package fr.moussax.blightedMC.utils.sound;

import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sequence of {@link BlightedSound} objects to be played in order.
 *
 * <p>Each sequence can be used to create complex audio effects in the game,
 * such as boss spawn sounds, item pickups, or forging effects.</p>
 *
 * <p>Sounds in the sequence are played in the order they appear using
 * {@link #play(Location)}.</p>
 */
public record SoundSequence(List<BlightedSound> sounds) {

    /**
     * Creates a new sound sequence from a list of {@link BlightedSound} objects.
     *
     * @param sounds list of sounds, copied to ensure immutability
     */
    public SoundSequence(List<BlightedSound> sounds) {
        this.sounds = List.copyOf(Objects.requireNonNull(sounds));
    }

    /**
     * Creates a new sound sequence from a varargs array of {@link BlightedSound} objects.
     *
     * @param sounds sounds to include in the sequence
     */
    public SoundSequence(BlightedSound... sounds) {
        this(Arrays.asList(sounds));
    }

    /**
     * Plays all sounds in this sequence at the given location in order.
     *
     * @param location the location where the sounds will be played
     */
    public void play(Location location) {
        for (BlightedSound sound : sounds) {
            sound.play(location);
        }
    }

    public static final SoundSequence SLAYER_BOSS_SPAWN = new SoundSequence(
        new BlightedSound(Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 2.0f, 1L),
        new BlightedSound(Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 1.8f, 4L),
        new BlightedSound(Sound.BLOCK_VAULT_OPEN_SHUTTER, 1.5f, 1.5f, 7L),
        new BlightedSound(Sound.BLOCK_VAULT_OPEN_SHUTTER, 1.5f, 1.3f, 10L),
        new BlightedSound(Sound.BLOCK_VAULT_OPEN_SHUTTER, 1.5f, 1.1f, 13L),
        new BlightedSound(Sound.BLOCK_VAULT_OPEN_SHUTTER, 1.5f, 0.9f, 16L),
        new BlightedSound(Sound.BLOCK_HEAVY_CORE_PLACE, 2.0f, 0.8f, 19L),
        new BlightedSound(Sound.BLOCK_HEAVY_CORE_PLACE, 2.0f, 0.6f, 22L),
        new BlightedSound(Sound.BLOCK_HEAVY_CORE_PLACE, 2.0f, 0.5f, 25L),
        new BlightedSound(Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 1.0f, 1.0f, 28L),
        new BlightedSound(Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f, 28L),
        new BlightedSound(Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.2f, 28L)
    );

    public static final SoundSequence BLIGHTED_BOSS_SPAWN = new SoundSequence(
        new BlightedSound(Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1.0f, 0.6f, 0L),
        new BlightedSound(Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 0.5f, 0L),
        new BlightedSound(Sound.ENTITY_WARDEN_EMERGE, 1.0f, 0.7f, 20L),
        new BlightedSound(Sound.ENTITY_BREEZE_INHALE, 1.0f, 0.5f, 40L),
        new BlightedSound(Sound.ENTITY_WARDEN_HEARTBEAT, 1.5f, 0.8f, 45L),
        new BlightedSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 0.6f, 60L),
        new BlightedSound(Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 1.5f, 1.0f, 60L),
        new BlightedSound(Sound.ITEM_TRIDENT_THUNDER, 1.0f, 0.7f, 60L)
    );

    public static final SoundSequence XP_PICKUP = new SoundSequence(
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f, 0L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.6f, 2L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.7f, 4L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.8f, 6L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.9f, 8L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.0f, 10L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.1f, 12L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f, 14L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.3f, 16L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.4f, 18L)
    );

    public static final SoundSequence BLIGHTED_GEMSTONE_CONSUME = new SoundSequence(
        new BlightedSound(Sound.BLOCK_POINTED_DRIPSTONE_HIT, 1.0f, 0.5f, 0L),
        new BlightedSound(Sound.BLOCK_POINTED_DRIPSTONE_HIT, 1.0f, 0.7f, 3L),
        new BlightedSound(Sound.BLOCK_POINTED_DRIPSTONE_HIT, 1.0f, 1.0f, 6L),
        new BlightedSound(Sound.BLOCK_POINTED_DRIPSTONE_HIT, 1.0f, 1.4f, 9L),
        new BlightedSound(Sound.BLOCK_POINTED_DRIPSTONE_HIT, 1.0f, 1.8f, 12L),
        new BlightedSound(Sound.BLOCK_VAULT_OPEN_SHUTTER, 1.0f, 1.2f, 14L),
        new BlightedSound(Sound.BLOCK_TRIAL_SPAWNER_BREAK, 1.0f, 0.9f, 18L),
        new BlightedSound(Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1.5f, 1.2f, 18L),
        new BlightedSound(Sound.ENTITY_GLOW_ITEM_FRAME_ADD_ITEM, 1.0f, 1.5f, 20L),
        new BlightedSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 2.0f, 22L)
    );

    public static final SoundSequence FORGE_ITEM = new SoundSequence(
        new BlightedSound(Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.0f, 0.50f, 0L),
        new BlightedSound(Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.0f, 0.55f, 2L),
        new BlightedSound(Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.0f, 0.60f, 4L),
        new BlightedSound(Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.0f, 0.65f, 6L),
        new BlightedSound(Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.0f, 0.70f, 8L),
        new BlightedSound(Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.0f, 0.80f, 10L),
        new BlightedSound(Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.0f, 0.90f, 12L),
        new BlightedSound(Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.0f, 1.00f, 14L),
        new BlightedSound(Sound.BLOCK_HEAVY_CORE_PLACE, 1.0f, 0.8f, 16L),
        new BlightedSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 0.8f, 18L),
        new BlightedSound(Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 0.5f, 18L)
    );
}
