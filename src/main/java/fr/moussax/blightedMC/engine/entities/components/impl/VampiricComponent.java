package fr.moussax.blightedMC.engine.entities.components.impl;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Vampiric component restoring entity health equal to a percentage of damage dealt to players.
 */
public final class VampiricComponent implements EntityComponent {

    private static final double LIFESTEAL_RATIO = 0.30;
    private int tickCounter = 0;

    @Override
    public String getId() {
        return "AFFIX_VAMPIRIC";
    }

    @Override
    public void onTick(BlightedEntity owner) {
        tickCounter++;
        if (tickCounter % 4 == 0) {
            LivingEntity entity = owner.getEntity();
            Particle.DustOptions crimsonDust = new Particle.DustOptions(Color.fromRGB(180, 0, 30), 1.0f);
            entity.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 1.0, 0), 3, 0.3, 0.4, 0.3, 0.0, crimsonDust);
        }
    }

    @Override
    public void onDealDamage(BlightedEntity owner, EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        LivingEntity entity = owner.getEntity();
        double lifestealAmount = event.getFinalDamage() * LIFESTEAL_RATIO;
        if (lifestealAmount <= 0) return;

        AttributeInstance maxHealthAttr = entity.getAttribute(Attribute.MAX_HEALTH);
        double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : entity.getHealth();
        entity.setHealth(Math.min(maxHealth, entity.getHealth() + lifestealAmount));

        entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation().add(0, 1.5, 0), 4, 0.3, 0.3, 0.3, 0.1);
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WITCH_DRINK, 0.6f, 1.2f);
    }
}
