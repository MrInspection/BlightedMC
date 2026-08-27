package fr.moussax.blightedMC.engine.entities.components.impl;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
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

/**
 * Directional blocking shield component for entities, blocking frontal attacks unless jump-crit.
 */
public class ShieldComponent implements EntityComponent {

    private final double arcDegrees;
    private long disableUntil = 0;
    private int tickCounter = 0;

    /**
     * Constructs a shield component with a specified blocking arc.
     *
     * @param arcDegrees blocking arc angle in degrees
     */
    public ShieldComponent(double arcDegrees) {
        this.arcDegrees = arcDegrees;
    }

    @Override
    public String getId() {
        return "AFFIX_SHIELD";
    }

    @Override
    public void onTick(BlightedEntity owner) {
        tickCounter++;
        if (tickCounter % 6 == 0 && System.currentTimeMillis() >= disableUntil) {
            LivingEntity entity = owner.getEntity();
            Location shoulder = entity.getLocation().add(0, 1.2, 0);
            entity.getWorld().spawnParticle(Particle.ENCHANTED_HIT, shoulder, 2, 0.2, 0.3, 0.2, 0.01);
        }
    }

    @Override
    public void onDamageTaken(BlightedEntity owner, EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity damager = event.getDamager();

        Entity source = (damager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) ? shooter : damager;
        Location location = defender.getLocation().add(0, 1.8, 0);

        if (source instanceof Player player && isAttackBlocked(defender, source)) {
            if (player.getLocation().getY() > player.getLocation().getBlockY() || player.getVelocity().getY() > 0.5) {
                disable(5000L);
                defender.getWorld().spawnParticle(Particle.EXPLOSION, location, 1, 0, 0, 0, 0);
                defender.getWorld().spawnParticle(Particle.WITCH, location, 30, 0.5, 0.5, 0.5, 0.1);
                defender.getWorld().playSound(location, Sound.ENTITY_ITEM_BREAK, 1.5f, 0.7f);
                return;
            }
        }

        if (isAttackBlocked(defender, source)) {
            event.setCancelled(true);
            defender.getWorld().spawnParticle(Particle.SWEEP_ATTACK, location, 1, 0, 0, 0, 0);
            defender.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, location, 20, 0.2, 0.2, 0.2, 0.1, Material.IRON_BLOCK.createBlockData());
            defender.getWorld().playSound(location, Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.2f);
        }
    }

    /**
     * Evaluates whether an incoming attack from the specified attacker is blocked by the shield.
     *
     * @param defender defender entity
     * @param attacker attacker entity
     * @return {@code true} if blocked, {@code false} otherwise
     */
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

    /**
     * Temporarily disables the shield for a duration in milliseconds.
     *
     * @param durationMillis disable duration in milliseconds
     */
    public void disable(long durationMillis) {
        this.disableUntil = System.currentTimeMillis() + durationMillis;
    }
}
