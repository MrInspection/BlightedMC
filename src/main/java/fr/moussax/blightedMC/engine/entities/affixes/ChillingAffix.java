package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class ChillingAffix implements EntityComponent {

    private static final double RADIUS = 6.0;
    private int tickCounter = 0;

    @Override
    public String getId() {
        return "AFFIX_CHILLING";
    }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {}

    @Override
    public void onTick(BlightedEntity owner) {
        tickCounter++;

        // Render aesthetic particles every 20 ticks
        if (tickCounter % 4 == 0) {
            owner.getEntity().getWorld().spawnParticle(Particle.SNOWFLAKE, owner.getEntity().getLocation().add(0, 1, 0), 15, RADIUS / 2, 0.5, RADIUS / 2, 0.0);
        }

        // Apply effect every 40 ticks
        if (tickCounter >= 8) {
            tickCounter = 0;
            applyChilling(owner.getEntity());
        }
    }

    private void applyChilling(LivingEntity entity) {
        for (org.bukkit.entity.Entity nearby : entity.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
            if (nearby instanceof Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, false, false, true));
            }
        }
    }
}
