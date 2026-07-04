package fr.moussax.blightedMC.engine.quest;

import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        if (targetBlock.getType() != Material.ENCHANTING_TABLE) {
            this.cancel();
            return;
        }

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

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        for (Enderman ritualist : summonedRitualists) {
            if (ritualist != null && ritualist.isValid()) {
                ritualist.remove();
            }
        }
        summonedRitualists.clear();
    }

    private void executeArrivalPhase() {
        double spawnRadius = 4.0;
        for (int index = 0; index < 8; index++) {
            double angle = index * (Math.PI / 4);
            double x = Math.cos(angle) * spawnRadius;
            double z = Math.sin(angle) * spawnRadius;

            Location spawnLocation = centerLocation.clone().add(x, -1.0, z);
            faceTarget(spawnLocation, centerLocation);

            Enderman ritualist = (Enderman) Objects.requireNonNull(centerLocation.getWorld()).spawnEntity(spawnLocation, EntityType.ENDERMAN);
            ritualist.setAI(false);
            ritualist.setSilent(true);
            ritualist.setInvulnerable(true);
            ritualist.setAware(false);
            ritualist.setPersistent(false);
            summonedRitualists.add(ritualist);

            centerLocation.getWorld().spawnParticle(Particle.PORTAL, spawnLocation.add(0, 1.0, 0), 30, 0.2, 1.0, 0.2, 0.5);
        }

        centerLocation.getWorld().playSound(centerLocation, Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 0.5f);
        centerLocation.getWorld().playSound(centerLocation, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.2f, 0.5f);
    }

    private void executeChannelingPhase() {
        double vortexRadius = 1.2 * (1.0 - (elapsedTicks / 70.0));
        double vortexAngle = elapsedTicks * 0.6;

        Location vortexLocation = centerLocation.clone().add(
                Math.cos(vortexAngle) * vortexRadius,
                (elapsedTicks / 35.0),
                Math.sin(vortexAngle) * vortexRadius
        );
        Objects.requireNonNull(centerLocation.getWorld()).spawnParticle(Particle.SOUL_FIRE_FLAME, vortexLocation, 1, 0, 0, 0, 0);

        for (Enderman ritualist : summonedRitualists) {
            if (!ritualist.isValid()) continue;

            Location handLocation = ritualist.getLocation().add(0, 1.8, 0);
            Vector direction = centerLocation.toVector().subtract(handLocation.toVector()).normalize();

            if (elapsedTicks % 3 == 0) {
                double distance = centerLocation.distance(handLocation);
                double step = (elapsedTicks % 10) / 10.0;
                Location beamPoint = handLocation.clone().add(direction.multiply(distance * step));
                centerLocation.getWorld().spawnParticle(Particle.WITCH, beamPoint, 1, 0, 0, 0, 0);
            }
        }

        if (elapsedTicks % 10 == 0) {
            float pitch = 0.5f + (elapsedTicks / 70.0f);
            centerLocation.getWorld().playSound(centerLocation, Sound.BLOCK_AMETHYST_CLUSTER_STEP, 0.8f, pitch);
        }
    }

    private void executeSacrificeWarning() {
        Objects.requireNonNull(centerLocation.getWorld()).playSound(centerLocation, Sound.ENTITY_ENDERMAN_STARE, 1.5f, 0.6f);
        centerLocation.getWorld().spawnParticle(Particle.SONIC_BOOM, centerLocation.clone().add(0, 1, 0), 1, 0, 0, 0, 0);
    }

    private void executeSacrificePull() {
        int pullDuration = 20;
        int currentPullTick = elapsedTicks - 70;
        double progress = (double) currentPullTick / pullDuration;

        double radius = 4.0 * (1.0 - Math.pow(progress, 2));
        double elevation = -1.0 + (progress * 2.0);

        Objects.requireNonNull(centerLocation.getWorld()).spawnParticle(
                Particle.PORTAL, centerLocation.clone().add(0, 1.0, 0),
                (int) (10 * progress), 0.1, 0.1, 0.1, 1.5
        );

        for (int i = 0; i < summonedRitualists.size(); i++) {
            Enderman ritualist = summonedRitualists.get(i);
            if (!ritualist.isValid()) continue;

            double angle = (i * (Math.PI / 4)) + (progress * Math.PI * 1.5);
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            Location newLocation = centerLocation.clone().add(x, elevation, z);
            faceTarget(newLocation, centerLocation);

            ritualist.teleport(newLocation);

            Vector velocity = newLocation.getDirection().multiply(-0.2);
            centerLocation.getWorld().spawnParticle(
                    Particle.SOUL_FIRE_FLAME, newLocation.clone().add(0, 1.5, 0),
                    0, velocity.getX(), velocity.getY(), velocity.getZ(), 0.1
            );
        }
    }

    private void executeManifestationClimax() {
        this.cancel();

        for (Player player : Objects.requireNonNull(centerLocation.getWorld()).getPlayers()) {
            if (player.getLocation().distanceSquared(centerLocation) < 4096) {
                player.playSound(centerLocation, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);
                player.playSound(centerLocation, Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 1.5f, 0.5f);
                player.playSound(centerLocation, Sound.ENTITY_WARDEN_SONIC_BOOM, 2.0f, 0.8f);
                player.stopSound(Sound.ENTITY_ENDERMAN_STARE);
            }
        }

        if (!player.isOnline()) return;

        Objects.requireNonNull(centerLocation.getWorld()).strikeLightningEffect(centerLocation);

        ItemStack rewardItem = ItemRegistry.getItem("BLIGHTED_WORKBENCH").toItemStack();
        centerLocation.getWorld().dropItem(targetBlock.getLocation().add(0, 1.5, 0), rewardItem);

        spawnClimaxParticles();
    }

    private void spawnClimaxParticles() {
        Objects.requireNonNull(centerLocation.getWorld()).spawnParticle(Particle.EXPLOSION, centerLocation, 2, 0.2, 0.2, 0.2, 0);
        centerLocation.getWorld().spawnParticle(Particle.LARGE_SMOKE, centerLocation, 40, 0.5, 0.5, 0.5, 0.15);

        for (int i = 0; i < 36; i++) {
            double angle = i * (Math.PI / 18);
            double x = Math.cos(angle);
            double z = Math.sin(angle);

            centerLocation.getWorld().spawnParticle(
                    Particle.END_ROD, centerLocation.clone().add(0, 0.5, 0),
                    0, x, 0.1, z, 0.35
            );
        }
    }

    private void faceTarget(Location source, Location target) {
        Vector lookDirection = target.toVector().subtract(source.toVector());
        if (lookDirection.lengthSquared() > 0.0001) {
            source.setDirection(lookDirection);
        }
    }
}
