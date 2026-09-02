package fr.moussax.blightedSMP.engine.entities.components.impl;

import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.components.EntityComponent;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Frenzied component granting continuous movement speed and attack fervor to the entity.
 */
public final class FrenziedComponent implements EntityComponent {

    private int tickCounter = 0;

    @Override
    public String getId() {
        return "AFFIX_FRENZIED";
    }

    @Override
    public void onTick(BlightedEntity owner) {
        tickCounter++;
        LivingEntity entity = owner.getEntity();

        if (tickCounter % 20 == 0) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, false, false, true));
        }

        if (tickCounter % 3 == 0) {
            entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, entity.getLocation().add(0, 0.2, 0), 2, 0.2, 0.1, 0.2, 0.02);
        }
    }
}
