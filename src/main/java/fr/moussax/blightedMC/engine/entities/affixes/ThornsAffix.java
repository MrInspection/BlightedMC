package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class ThornsAffix implements EntityComponent {

    private static final double REFLECTION_PERCENTAGE = 0.25;

    @Override
    public String getId() {
        return "AFFIX_THORNS";
    }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {}

    @Override
    public void onDamageTaken(BlightedEntity owner, EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        LivingEntity target = null;

        if (damager instanceof LivingEntity living) {
            target = living;
        } else if (damager instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter) {
            target = shooter;
        }

        if (target == null || target == owner.getEntity()) return;

        double reflected = event.getDamage() * REFLECTION_PERCENTAGE;
        if (reflected < 1.0) return;

        target.damage(reflected, owner.getEntity());

        owner.getEntity().getWorld().spawnParticle(Particle.CRIT, owner.getEntity().getLocation().add(0, 1, 0), 10, 0.4, 0.4, 0.4, 0.1);
        owner.getEntity().getWorld().playSound(owner.getEntity().getLocation(), Sound.ENCHANT_THORNS_HIT, 1.0f, 1.0f);
    }
}
