package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class BurningAffix implements EntityComponent {

    private static final double BURN_RADIUS = 3.0;
    private static final double BASE_DAMAGE = 2.0;
    private Map<UUID, Long> playerHeatMap = new HashMap<>();
    private int tickCounter = 0;

    @Override
    public String getId() { return "AFFIX_BURNING"; }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {}

    @Override
    public void onTick(BlightedEntity owner) {
        tickCounter++;
        LivingEntity entity = owner.getEntity();

        drawVolcanicAura(entity.getLocation(), tickCounter);

        if (tickCounter % 5 == 0) {
            applyAuraDamage(entity);
        }
    }

    private void drawVolcanicAura(Location center, int tick) {
        for (int i = 0; i < 24; i++) {
            double angle = (tick * 0.1) + (i * Math.PI * 2 / 24);
            double x = Math.cos(angle) * BURN_RADIUS;
            double z = Math.sin(angle) * BURN_RADIUS;

            Color color = (i % 2 == 0) ? Color.fromRGB(255, 69, 0) : Color.fromRGB(139, 0, 0);
            Particle.DustOptions dust = new Particle.DustOptions(color, 1.5f);

            Objects.requireNonNull(center.getWorld()).spawnParticle(Particle.DUST, center.clone().add(x, 0.1, z), 1, 0, 0, 0, 0, dust);
        }

        double rx = ThreadLocalRandom.current().nextDouble(-BURN_RADIUS, BURN_RADIUS);
        double rz = ThreadLocalRandom.current().nextDouble(-BURN_RADIUS, BURN_RADIUS);

        center.getWorld().spawnParticle(Particle.LAVA, center.clone().add(rx, 0.2, rz), 1, 0.1, 0.1, 0.1, 0.05);
        center.getWorld().spawnParticle(Particle.FLAME, center.clone().add(rx, 0.3, rz), 3, 0.2, 0.2, 0.2, 0.02);
    }

    private void applyAuraDamage(LivingEntity entity) {
        for (org.bukkit.entity.Entity nearby : entity.getNearbyEntities(BURN_RADIUS, 2.0, BURN_RADIUS)) {
            if (!(nearby instanceof Player player)) continue;
            player.setFireTicks(40);
            player.damage(BASE_DAMAGE, entity);
            entity.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 10, 0.2, 0.5, 0.2, 0.05);
            entity.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 0.5f, 0.6f);
        }
    }

    @Override
    public EntityComponent clone() {
        try {
            BurningAffix clone = (BurningAffix) super.clone();
            clone.playerHeatMap = new HashMap<>(this.playerHeatMap);
            clone.tickCounter = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
