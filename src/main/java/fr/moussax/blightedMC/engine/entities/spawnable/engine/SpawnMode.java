package fr.moussax.blightedMC.engine.entities.spawnable.engine;

/**
 * Defines how a custom spawnable entity integrates with Minecraft spawning mechanisms.
 */
public enum SpawnMode {

    /**
     * Intercepts vanilla entity spawning attempts of the matching entity type.
     *
     * <p>Requires vanilla Minecraft to attempt spawning the underlying entity type naturally.</p>
     */
    REPLACEMENT,

    /**
     * Spawns independently using chunk-based environmental scanning.
     *
     * <p>Operates independently of vanilla spawn attempt frequencies.</p>
     */
    INDEPENDENT,

    /**
     * Uses both vanilla replacement interception and independent chunk-based scanning.
     */
    HYBRID
}
