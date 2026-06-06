package fr.moussax.blightedMC.engine.entities.components;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EntityComponent extends Cloneable {

    String getId();
    void onInit(LivingEntity entity);
    void onDestroy(LivingEntity entity);

    default void onTick(BlightedEntity owner) {}
    default void onDamageTaken(BlightedEntity owner, EntityDamageByEntityEvent event) {}
    default void onDealDamage(BlightedEntity owner, EntityDamageByEntityEvent event) {}
    default void onDeath(BlightedEntity owner, Location location) {}

    EntityComponent clone();
}
