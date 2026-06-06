package fr.moussax.blightedMC.engine.entities.listeners;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.BlightedShieldComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener.getBlightedEntity;

public final class EntityComponentListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        BlightedEntity wrapper = getBlightedEntity(event.getEntity());
        if (wrapper == null) return;

        BlightedShieldComponent shield = wrapper.getComponent("BLIGHTED_SHIELD");
        if (shield == null) return;

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        Entity source = (damager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) ? shooter : damager;
        Location location = entity.getLocation().add(0, 1.8, 0);

        if (source instanceof Player player && shield.isAttackBlocked(entity, source)) {
            if (player.getLocation().getY() > player.getLocation().getBlockY() || player.getVelocity().getY() > 0) {
                shield.disable(5000L);
                entity.getWorld().spawnParticle(Particle.EXPLOSION, location, 1, 0, 0, 0, 0);
                entity.getWorld().spawnParticle(Particle.WITCH, location, 30, 0.5, 0.5, 0.5, 0.1);
                entity.getWorld().playSound(location, Sound.ENTITY_ITEM_BREAK, 1.5f, 0.7f);
                return;
            }
        }

        if (shield.isAttackBlocked(entity, source)) {
            event.setCancelled(true);

            entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
            entity.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, location, 20, 0.2, 0.2, 0.2, 0.1, Material.IRON_BLOCK.createBlockData());
            entity.getWorld().playSound(location, Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.2f);
        }
    }
}
