package fr.moussax.blightedMC.engine.entities.rituals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public final class RitualAnimations {

    public static void playRiteAnimation(
            JavaPlugin instance,
            Location spawnLocation,
            @NonNull Runnable onAnimationComplete
    ) {
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 60) {
                    this.cancel();
                    onAnimationComplete.run();
                    return;
                }

                if (tick < 20) {
                    drawRitualFloor(spawnLocation, tick);
                }

                if (tick >= 20 && tick < 45) {
                    drawRisingSoulVortex(spawnLocation, tick);
                }

                if (tick >= 45) {
                    drawManifestation(spawnLocation, tick);
                }

                tick++;
            }
        }.runTaskTimer(instance, 0L, 1L);
    }

    public static void playCollapseAnimation(
            JavaPlugin instance,
            Location collapseLocation,
            @NonNull Runnable onAnimationComplete
    ) {
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 40) {
                    this.cancel();
                    onAnimationComplete.run();
                    return;
                }

                if (tick < 15) {
                    drawFractureAndPull(collapseLocation, tick);
                }

                if (tick >= 15 && tick < 35) {
                    drawVoidVortexAndImplosion(collapseLocation, tick);
                }

                if (tick >= 35) {
                    drawFinalShockwave(collapseLocation);
                }

                tick++;
            }
        }.runTaskTimer(instance, 0L, 1L);
    }

    private static void drawRitualFloor(Location location, int tick) {
        double radius = 3.0;
        int particles = 4;
        double speed = 8.0;

        for (int i = 0; i < particles; i++) {
            double angle = Math.toRadians((tick * speed) + (i * (360.0 / particles)));
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, location.clone().add(x, 0.1, z), 1, 0, 0, 0, 0);
            location.getWorld().spawnParticle(Particle.SOUL, location.clone().add(x * 0.5, 0.1, z * 0.5), 0, 0, 0.1, 0, 0.05);
        }

        if (tick % 5 == 0) {
            Objects.requireNonNull(location.getWorld()).spawnParticle(
                    Particle.BLOCK, location.clone().add(0, 0.1, 0), 5, 0.5, 0, 0.5, 0.1,
                    Material.CRYING_OBSIDIAN.createBlockData()
            );
        }
    }

    private static void drawRisingSoulVortex(Location location, int tick) {
        double radius = 1.8;
        double y = ((tick - 20) / 25.0) * 3.0;

        for (int i = 0; i < 2; i++) {
            double angle = Math.toRadians((tick * 25.0) + (i * 180));
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location particleLocation = location.clone().add(x, y, z);

            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.SCULK_SOUL, particleLocation, 1, 0, 0, 0, 0.02);
            location.getWorld().spawnParticle(Particle.WITCH, particleLocation, 0, 0, 0, 0, 0);
        }

        if (tick % 2 == 0) {
            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.REVERSE_PORTAL, location.clone().add(0, y, 0), 1, 0.1, 0.5, 0.1, 0.01);
        }
    }

    private static void drawManifestation(Location location, int tick) {
        Location center = location.clone().add(0, 1.2, 0);
        Objects.requireNonNull(center.getWorld()).spawnParticle(Particle.WITCH, center, 5, 0.2, 0.5, 0.2, 0);
        center.getWorld().spawnParticle(Particle.REVERSE_PORTAL, center, 2, 0.1, 0.1, 0.1, 0.05);

        for (int i = 0; i < 2; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = 2.0;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            double yOffset = (Math.random() - 0.5) * 2;

            Location runeStart = center.clone().add(x, yOffset, z);
            Vector direction = center.toVector().subtract(runeStart.toVector()).normalize().multiply(0.3);
            center.getWorld().spawnParticle(Particle.ENCHANT, runeStart, 0, direction.getX(), direction.getY(), direction.getZ(), 1);
        }
    }

    private static void drawFractureAndPull(Location location, int tick) {
        World world = Objects.requireNonNull(location.getWorld());
        Location center = location.clone().add(0, 1.2, 0);

        // Fracturing glass and crying obsidian fragments bursting
        world.spawnParticle(Particle.BLOCK, center, 4, 0.4, 0.6, 0.4, 0.1, Material.CRYING_OBSIDIAN.createBlockData());
        world.spawnParticle(Particle.BLOCK, center, 3, 0.4, 0.6, 0.4, 0.1, Material.TINTED_GLASS.createBlockData());

        // Inward void collapsing ring
        double radius = Math.max(0.3, 2.5 - (tick * 0.15));
        for (int i = 0; i < 3; i++) {
            double angle = Math.toRadians((tick * 20.0) + (i * 120));
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            world.spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, center.clone().add(x, 0, z), 1, 0, 0, 0, 0);
        }
    }

    private static void drawVoidVortexAndImplosion(Location location, int tick) {
        World world = Objects.requireNonNull(location.getWorld());
        Location center = location.clone().add(0, 1.2, 0);

        // Accelerating inward vortex
        double radius = Math.max(0.2, 1.8 - ((tick - 15) * 0.08));
        for (int i = 0; i < 3; i++) {
            double angle = Math.toRadians((tick * 35.0) + (i * 120));
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            double y = Math.sin((tick + i) * 0.4) * 0.8;

            Location particleLoc = center.clone().add(x, y, z);
            Vector inward = center.toVector().subtract(particleLoc.toVector()).normalize().multiply(0.25);

            world.spawnParticle(Particle.PORTAL, particleLoc, 0, inward.getX(), inward.getY(), inward.getZ(), 0.8);
            world.spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 0, inward.getX(), inward.getY(), inward.getZ(), 0.05);
        }

        world.spawnParticle(Particle.REVERSE_PORTAL, center, 4, 0.1, 0.1, 0.1, 0.08);
    }

    private static void drawFinalShockwave(Location location) {
        World world = Objects.requireNonNull(location.getWorld());
        Location center = location.clone().add(0, 1.0, 0);

        world.spawnParticle(Particle.SONIC_BOOM, center, 1);
        world.spawnParticle(Particle.SOUL, center, 20, 0.5, 0.5, 0.5, 0.15);
        world.spawnParticle(Particle.SMOKE, center, 15, 0.3, 0.3, 0.3, 0.05);
    }
}
