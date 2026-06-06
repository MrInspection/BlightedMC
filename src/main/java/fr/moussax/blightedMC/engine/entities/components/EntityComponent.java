package fr.moussax.blightedMC.engine.entities.components;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EntityComponent {

    String getId();

    void onInit(LivingEntity entity);

    void onDestroy(LivingEntity entity);

    /**
     * Called every 5 ticks (0.25s) by the BlightedEntity core lifecycle.
     * Replaces standalone BukkitRunnables.
     */
    default void onTick(BlightedEntity owner) {}

    /**
     * Called when the entity receives damage.
     */
    default void onDamageTaken(BlightedEntity owner, EntityDamageByEntityEvent event) {}

    /**
     * Called when the entity successfully damages another entity.
     */
    default void onDealDamage(BlightedEntity owner, EntityDamageByEntityEvent event) {}

    /**
     * Called upon the entity's death.
     */
    default void onDeath(BlightedEntity owner, Location location) {}
}
