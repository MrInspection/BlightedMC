package fr.moussax.blightedMC.smp.core.entities.spawnable.engine;

/**
 * Defines how a spawnable entity interacts with vanilla spawning.
 */
public enum SpawnMode {
    /**
     * Hijacks vanilla spawn attempts of the matching entity type.
     * Requires vanilla to attempt spawning that entity type naturally.
     */
    REPLACEMENT,

    /**
     * Spawns independently using chunk-based scanning.
     * Not dependent on vanilla spawn attempts.
     */
    INDEPENDENT,

    /**
     * Uses both replacement and independent spawning mechanisms.
     */
    HYBRID
}