package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class GravityAffix implements EntityComponent {

    private static final double PULL_RADIUS = 8.0;
    private static final double PULL_STRENGTH = 0.55;
    private int tickCounter = 0;

    @Override
    public String getId() {
        return "AFFIX_GRAVITY";
    }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {}

    @Override
    public void onTick(BlightedEntity owner) {
        tickCounter++;

        if (tickCounter % 2 == 0) {
            owner.getEntity().getWorld().spawnParticle(Particle.PORTAL, owner.getEntity().getLocation().add(0, 1, 0), 10, 1.0, 1.0, 1.0, -0.5);
        }

        if (tickCounter >= 12) {
            tickCounter = 0;
            performGravityPulse(owner.getEntity());
        }
    }

    private void performGravityPulse(LivingEntity entity) {
        Location center = entity.getLocation();
        entity.getWorld().spawnParticle(Particle.REVERSE_PORTAL, center.add(0, 1, 0), 60, 3, 0.5, 3, 0.05);
        entity.getWorld().playSound(center, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 0.5f);
        entity.getWorld().playSound(center, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.8f, 0.7f);

        for (org.bukkit.entity.Entity nearby : entity.getNearbyEntities(PULL_RADIUS, PULL_RADIUS, PULL_RADIUS)) {
            if (!(nearby instanceof Player player)) continue;

            Vector pull = center.toVector().subtract(player.getLocation().toVector()).normalize().multiply(PULL_STRENGTH);
            player.setVelocity(player.getVelocity().add(pull));
        }
    }
}
