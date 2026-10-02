package fr.moussax.blightedSMP.engine.entities.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Utility functions for dispatching sound effects, particle clouds, and animation states.
 */
public final class EntityEffects {

    private EntityEffects() {
    }

    /**
     * Plays a sound effect at the entity's current location.
     *
     * @param entity target entity
     * @param sound  sound to play
     * @param volume volume level
     * @param pitch  pitch modifier
     */
    public static void playSound(@Nullable LivingEntity entity, @NonNull Sound sound, float volume, float pitch) {
        Objects.requireNonNull(sound, "sound cannot be null");
        if (entity != null && entity.isValid()) {
            entity.getWorld().playSound(entity.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * Plays a sound effect at a specific world location.
     *
     * @param location location to play the sound at
     * @param sound    sound to play
     * @param volume   volume level
     * @param pitch    pitch modifier
     */
    public static void playSound(@NonNull Location location, @NonNull Sound sound, float volume, float pitch) {
        Objects.requireNonNull(location, "location cannot be null");
        Objects.requireNonNull(sound, "sound cannot be null");
        if (location.getWorld() != null) {
            location.getWorld().playSound(location, sound, volume, pitch);
        }
    }

    /**
     * Spawns particles at the entity's current location.
     *
     * @param entity   target entity
     * @param particle particle type
     * @param count    number of particles
     */
    public static void spawnParticle(@Nullable LivingEntity entity, @NonNull Particle particle, int count) {
        Objects.requireNonNull(particle, "particle cannot be null");
        if (entity != null && entity.isValid()) {
            entity.getWorld().spawnParticle(particle, entity.getLocation(), count);
        }
    }

    /**
     * Spawns particles at the entity's current location with random positional offsets.
     *
     * @param entity   target entity
     * @param particle particle type
     * @param count    number of particles
     * @param offsetX  maximum X axis offset
     * @param offsetY  maximum Y axis offset
     * @param offsetZ  maximum Z axis offset
     */
    public static void spawnParticle(
            @Nullable LivingEntity entity,
            @NonNull Particle particle,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ
    ) {
        Objects.requireNonNull(particle, "particle cannot be null");
        if (entity != null && entity.isValid()) {
            entity.getWorld().spawnParticle(particle, entity.getLocation(), count, offsetX, offsetY, offsetZ);
        }
    }

    /**
     * Spawns particles at the entity's current location with custom particle data.
     *
     * @param entity   target entity
     * @param particle particle type
     * @param count    number of particles
     * @param data     particle data object, or null
     * @param <T>      particle data type
     */
    public static <T> void spawnParticle(
            @Nullable LivingEntity entity,
            @NonNull Particle particle,
            int count,
            @Nullable T data
    ) {
        Objects.requireNonNull(particle, "particle cannot be null");
        if (entity != null && entity.isValid()) {
            entity.getWorld().spawnParticle(particle, entity.getLocation(), count, 0.0, 0.0, 0.0, data);
        }
    }

    /**
     * Spawns particles at a specific world location.
     *
     * @param location location to spawn particles at
     * @param particle particle type
     * @param count    number of particles
     */
    public static void spawnParticle(@NonNull Location location, @NonNull Particle particle, int count) {
        Objects.requireNonNull(location, "location cannot be null");
        Objects.requireNonNull(particle, "particle cannot be null");
        if (location.getWorld() != null) {
            location.getWorld().spawnParticle(particle, location, count);
        }
    }

    /**
     * Spawns particles at a specific world location with custom particle data.
     *
     * @param location location to spawn particles at
     * @param particle particle type
     * @param count    number of particles
     * @param data     particle data object, or null
     * @param <T>      particle data type
     */
    public static <T> void spawnParticle(
            @NonNull Location location,
            @NonNull Particle particle,
            int count,
            @Nullable T data
    ) {
        Objects.requireNonNull(location, "location cannot be null");
        Objects.requireNonNull(particle, "particle cannot be null");
        if (location.getWorld() != null) {
            location.getWorld().spawnParticle(particle, location, count, 0.0, 0.0, 0.0, data);
        }
    }

    /**
     * Triggers the entity's main-hand swinging animation.
     *
     * @param entity target entity
     */
    public static void swingMainHand(@Nullable LivingEntity entity) {
        if (entity != null && entity.isValid()) {
            entity.swingMainHand();
        }
    }

    /**
     * Triggers the entity's off-hand swinging animation.
     *
     * @param entity target entity
     */
    public static void swingOffHand(@Nullable LivingEntity entity) {
        if (entity != null && entity.isValid()) {
            entity.swingOffHand();
        }
    }
}
