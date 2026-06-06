package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class BurningAffix implements EntityComponent {

    private static final double BURN_RADIUS = 3.0;
    private static final int FIRE_TICKS = 60; // 3 seconds of fire
    private int tickCounter = 0;

    @Override
    public String getId() {
        return "AFFIX_BURNING";
    }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {}

    @Override
    public void onTick(BlightedEntity owner) {
        tickCounter++;
        LivingEntity entity = owner.getEntity();
        Location center = entity.getLocation();

        if (tickCounter % 2 == 0) {
            drawFireAura(center);
        }

        if (tickCounter >= 2) {
            tickCounter = 0;
            applyCombustion(entity);
        }
    }

    private void drawFireAura(Location center) {
        Objects.requireNonNull(center.getWorld()).spawnParticle(Particle.FLAME, center.add(0, 0.1, 0), 8, BURN_RADIUS / 2, 0.1, BURN_RADIUS / 2, 0.02);
        center.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, center, 2, BURN_RADIUS / 2, 0.5, BURN_RADIUS / 2, 0.01);
    }

    private void applyCombustion(LivingEntity entity) {
        boolean ignitedSomeone = false;

        for (org.bukkit.entity.Entity nearby : entity.getNearbyEntities(BURN_RADIUS, 2.0, BURN_RADIUS)) {
            if (!(nearby instanceof Player player)) continue;

            if (player.getFireTicks() <= 0) {
                player.setFireTicks(FIRE_TICKS);
                ignitedSomeone = true;
            }
        }

        if (ignitedSomeone) {
            entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.6f, 1.2f);
        }
    }
}
