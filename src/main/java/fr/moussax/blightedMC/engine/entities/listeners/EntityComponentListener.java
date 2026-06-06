package fr.moussax.blightedMC.engine.entities.listeners;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener.getBlightedEntity;

public final class EntityComponentListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity rawDamager = event.getDamager();
        Entity source = (rawDamager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter)
                ? shooter
                : rawDamager;

        BlightedEntity victimWrapper = getBlightedEntity(victim);
        if (victimWrapper != null) {
            for (EntityComponent component : victimWrapper.getComponents()) {
                component.onDamageTaken(victimWrapper, event);
            }
        }

        BlightedEntity attackerWrapper = getBlightedEntity(source);
        if (attackerWrapper != null) {
            for (EntityComponent component : attackerWrapper.getComponents()) {
                component.onDealDamage(attackerWrapper, event);
            }
        }
    }
}
