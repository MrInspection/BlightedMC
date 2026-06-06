package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public final class DeflectAffix implements EntityComponent {

    private static final double DEFLECT_CHANCE = 0.45;

    @Override
    public String getId() {
        return "AFFIX_DEFLECT";
    }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {}

    @Override
    public void onDamageTaken(BlightedEntity owner, EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (ThreadLocalRandom.current().nextDouble() > DEFLECT_CHANCE) return;

        event.setCancelled(true);

        LivingEntity defender = owner.getEntity();

        defender.getWorld().spawnParticle(Particle.WITCH, defender.getLocation().add(0, 1.2, 0), 20, 0.4, 0.4, 0.4, 0.1);
        defender.getWorld().spawnParticle(Particle.SWEEP_ATTACK, defender.getLocation().add(0, 1.2, 0), 1, 0, 0, 0, 0);
        defender.getWorld().playSound(defender.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.2f, 1.5f);
        defender.getWorld().playSound(defender.getLocation(), Sound.ENCHANT_THORNS_HIT, 1.0f, 0.8f);

        if (projectile.getShooter() instanceof LivingEntity shooter) {
            Vector returnTrajectory = shooter.getEyeLocation().toVector()
                    .subtract(projectile.getLocation().toVector())
                    .normalize()
                    .multiply(projectile.getVelocity().length() * 1.3); // 30% speed boost on return

            projectile.setVelocity(returnTrajectory);
            projectile.setShooter(defender);
        } else {
            projectile.setVelocity(projectile.getVelocity().multiply(-1));
            projectile.setShooter(defender);
        }
    }
}
