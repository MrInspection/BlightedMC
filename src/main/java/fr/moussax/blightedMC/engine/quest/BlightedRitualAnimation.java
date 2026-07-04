package fr.moussax.blightedMC.engine.quest;

import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BlightedRitualAnimation extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final Player player;
    private final Block targetBlock;
    private final Location centerLocation;
    private final List<Enderman> summonedRitualists = new ArrayList<>();

    private int elapsedTicks = 0;

    public BlightedRitualAnimation(JavaPlugin plugin, Player player, Block targetBlock) {
        this.plugin = plugin;
        this.player = player;
        this.targetBlock = targetBlock;
        this.centerLocation = targetBlock.getLocation().add(0.5, 1.0, 0.5);
    }

    @Override
    public void run() {
        if (elapsedTicks == 0) {
            executeArrivalPhase();
        } else if (elapsedTicks < 70) {
            executeChannelingPhase();
        } else if (elapsedTicks == 70) {
            executeSacrificeWarning();
        } else if (elapsedTicks < 90) {
            executeSacrificePull();
        } else {
            executeManifestationClimax();
        }
        elapsedTicks++;
    }

    private void executeArrivalPhase() {
        double spawnRadius = 4.0;
        for (int index = 0; index < 8; index++) {
            double angleInRadians = index * (Math.PI / 4);
            double xOffset = Math.cos(angleInRadians) * spawnRadius;
            double zOffset = Math.sin(angleInRadians) * spawnRadius;

            Location endermanLocation = centerLocation.clone().add(xOffset, -1.0, zOffset);
            endermanLocation.setDirection(centerLocation.toVector().subtract(endermanLocation.toVector()));

            Enderman ritualist = (Enderman) Objects.requireNonNull(centerLocation.getWorld()).spawnEntity(endermanLocation, EntityType.ENDERMAN);
            ritualist.setAI(false);
            ritualist.setSilent(true);
            ritualist.setInvulnerable(true);
            ritualist.setAware(false);
            summonedRitualists.add(ritualist);

            centerLocation.getWorld().spawnParticle(Particle.PORTAL, endermanLocation.add(0, 1.0, 0), 20, 0.5, 1.0, 0.5, 0.1);
        }
        centerLocation.getWorld().playSound(centerLocation, Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 0.5f);
        centerLocation.getWorld().playSound(centerLocation, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.2f, 0.5f);
    }

    private void executeChannelingPhase() {
        for (Enderman ritualist : summonedRitualists) {
            if (!ritualist.isValid()) continue;

            Location handLocation = ritualist.getLocation().add(0, 1.8, 0);
            Vector directionVector = centerLocation.toVector().subtract(handLocation.toVector()).normalize();

            for (double trailStep = 0; trailStep < 1.0; trailStep += 0.2) {
                Location trailParticleLocation = handLocation.clone().add(directionVector.clone().multiply(trailStep * centerLocation.distance(handLocation)));
                Objects.requireNonNull(centerLocation.getWorld()).spawnParticle(Particle.WITCH, trailParticleLocation, 1, 0, 0, 0, 0);
            }
        }

        double spiralAngle = elapsedTicks * 0.5;
        double xOffset = Math.cos(spiralAngle) * 0.8;
        double zOffset = Math.sin(spiralAngle) * 0.8;
        double yOffset = (elapsedTicks / 70.0) * 2.0;

        Location flameLocation = centerLocation.clone().add(xOffset, yOffset, zOffset);
        Objects.requireNonNull(centerLocation.getWorld()).spawnParticle(Particle.SOUL_FIRE_FLAME, flameLocation, 2, 0, 0, 0, 0.02);
        centerLocation.getWorld().spawnParticle(Particle.PORTAL, centerLocation, 5, 0.2, 0.5, 0.2, 0.1);

        int soundInterval = Math.max(2, 10 - (elapsedTicks / 10));
        if (elapsedTicks % soundInterval == 0) {
            float soundPitch = 0.5f + (elapsedTicks / 70.0f);
            centerLocation.getWorld().playSound(centerLocation, Sound.BLOCK_AMETHYST_CLUSTER_STEP, 0.8f, soundPitch);
        }
    }

    private void executeSacrificeWarning() {
        Objects.requireNonNull(centerLocation.getWorld()).playSound(centerLocation, Sound.ENTITY_ENDERMAN_STARE, 1.5f, 0.6f);
    }

    private void executeSacrificePull() {
        for (Enderman ritualist : summonedRitualists) {
            if (!ritualist.isValid()) continue;

            Location currentLocation = ritualist.getLocation();
            double distance = centerLocation.distance(currentLocation);
            if (distance < 0.25) continue;

            Vector pullVector = centerLocation.toVector().subtract(currentLocation.toVector()).normalize().multiply(0.25);
            Location newLocation = currentLocation.clone().add(pullVector);

            Vector direction = centerLocation.toVector().subtract(newLocation.toVector());
            if (direction.lengthSquared() > 0.0001) {
                newLocation.setDirection(direction);
            }

            ritualist.teleport(newLocation);

            Location particleLocation = ritualist.getLocation().add(0, 1.0, 0);
            Objects.requireNonNull(centerLocation.getWorld()).spawnParticle(
                    Particle.REVERSE_PORTAL, particleLocation, 10, 0.3, 0.5, 0.3, 0.1
            );
        }
    }

    private void executeManifestationClimax() {
        this.cancel();

        for (Enderman ritualist : summonedRitualists) {
            if (ritualist.isValid()) {
                Location deathParticleLocation = ritualist.getLocation().add(0, 1.0, 0);
                try {
                    Objects.requireNonNull(centerLocation.getWorld()).spawnParticle(Particle.SOUL, deathParticleLocation, 15, 0.2, 0.5, 0.2, 0.05);
                } catch (IllegalArgumentException exception) {
                    plugin.getLogger().warning("Ritualist death particle failed (registry mismatch?): " + exception.getMessage());
                }
                ritualist.remove();
            }
        }

        if (!player.isOnline()) return;
        Objects.requireNonNull(centerLocation.getWorld()).strikeLightningEffect(centerLocation);
        centerLocation.getWorld().dropItem(targetBlock.getLocation().add(0, 1.5, 0), ItemRegistry.getItem("BLIGHTED_WORKBENCH").toItemStack());
        centerLocation.getWorld().playSound(centerLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 0.8f);
        centerLocation.getWorld().playSound(centerLocation, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 0.9f);
        spawnClimaxParticles();
    }

    private void spawnClimaxParticles() {
        try {
            centerLocation.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, centerLocation, 1);
            centerLocation.getWorld().spawnParticle(Particle.FLASH, centerLocation, 3);
            centerLocation.getWorld().spawnParticle(Particle.CHERRY_LEAVES, centerLocation, 60, 0.5, 0.5, 0.5, 0.1);
        } catch (IllegalArgumentException exception) {
            plugin.getLogger().warning("Climax particle failed (registry mismatch?): " + exception.getMessage());
        }
    }
}
