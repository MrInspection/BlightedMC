package fr.moussax.blightedMC.engine.entities.components;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class ShieldComponent implements EntityComponent {
    private final double arcDegrees;
    private long disableUntil = 0;

    public ShieldComponent(double arcDegrees) {
        this.arcDegrees = arcDegrees;
    }

    @Override
    public String getId() {
        return "BLIGHTED_SHIELD";
    }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {}

    @Override
    public void onDamageTaken(BlightedEntity owner, EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        Entity source = (damager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) ? shooter : damager;
        Location location = entity.getLocation().add(0, 1.8, 0);

        if (source instanceof Player player && isAttackBlocked(entity, source)) {
            if (player.getLocation().getY() > player.getLocation().getBlockY() || player.getVelocity().getY() > 0) {
                disable(5000L);
                entity.getWorld().spawnParticle(Particle.EXPLOSION, location, 1, 0, 0, 0, 0);
                entity.getWorld().spawnParticle(Particle.WITCH, location, 30, 0.5, 0.5, 0.5, 0.1);
                entity.getWorld().playSound(location, Sound.ENTITY_ITEM_BREAK, 1.5f, 0.7f);
                return;
            }
        }

        if (isAttackBlocked(entity, source)) {
            event.setCancelled(true);
            entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
            entity.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, location, 20, 0.2, 0.2, 0.2, 0.1, Material.IRON_BLOCK.createBlockData());
            entity.getWorld().playSound(location, Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.2f);
        }
    }

    public boolean isAttackBlocked(Entity defender, Entity attacker) {
        if (System.currentTimeMillis() < disableUntil) return false;

        Location defenderLocation = defender.getLocation();
        Location attackerLocation = attacker.getLocation();

        Vector defenderDirection = defenderLocation.getDirection();
        Vector attackerDirection = attackerLocation.toVector()
                .subtract(defenderLocation.toVector()).normalize();

        double dotProduct = defenderDirection.dot(attackerDirection);
        double angle = Math.toDegrees(Math.acos(dotProduct));
        return angle <= (arcDegrees / 2.0);
    }

    public void disable(long durationMillis) {
        this.disableUntil = System.currentTimeMillis() + durationMillis;
    }
}
