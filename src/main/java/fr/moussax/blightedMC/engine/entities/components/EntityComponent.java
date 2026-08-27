package fr.moussax.blightedMC.engine.entities.components;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Modular behavior or affix attached to a {@link BlightedEntity}.
 *
 * <p>Implementations receive lifecycle notifications, periodic tick updates, and combat event callbacks
 * while attached to an active entity.</p>
 */
public interface EntityComponent {

    /**
     * Unique identifier for this component type.
     *
     * @return unique component identifier
     */
    String getId();

    /**
     * Invoked when this component is initialized and attached to a living entity.
     *
     * @param entity underlying Bukkit living entity
     */
    default void onInit(LivingEntity entity) {
    }

    /**
     * Invoked when this component is detached or destroyed.
     *
     * @param entity underlying Bukkit living entity
     */
    default void onDestroy(LivingEntity entity) {
    }

    /**
     * Periodic tick callback invoked while the host entity is active.
     *
     * @param owner owner blighted entity wrapper
     */
    default void onTick(BlightedEntity owner) {
    }

    /**
     * Callback invoked when the host entity receives generic damage.
     *
     * @param owner owner blighted entity wrapper
     * @param event damage event details
     */
    default void onDamageTaken(BlightedEntity owner, EntityDamageEvent event) {
    }

    /**
     * Callback invoked when the host entity receives damage from another entity.
     *
     * @param owner owner blighted entity wrapper
     * @param event damage-by-entity event details
     */
    default void onDamageTaken(BlightedEntity owner, EntityDamageByEntityEvent event) {
    }

    /**
     * Callback invoked when the host entity inflicts damage on another entity.
     *
     * @param owner owner blighted entity wrapper
     * @param event damage-by-entity event details
     */
    default void onDealDamage(BlightedEntity owner, EntityDamageByEntityEvent event) {
    }

    /**
     * Callback invoked when the host entity dies.
     *
     * @param owner    owner blighted entity wrapper
     * @param location location of entity death
     */
    default void onDeath(BlightedEntity owner, Location location) {
    }
}
