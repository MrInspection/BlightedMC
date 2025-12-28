package fr.moussax.blightedMC.smp.core.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.smp.core.fishing.FishingMethod;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LavaFishingHook {
    private static final Map<UUID, LavaFishingHook> ACTIVE_HOOKS = new HashMap<>();
    private static final double FLOAT_TARGET_HIGH = 0.85;
    private static final double FLOAT_TARGET_LOW = 0.6;
    private static final double PARTICLE_SPEED = 0.15;
    private static final double PARTICLE_DISTANCE_THRESHOLD = 0.25;
    private static final int BITE_WINDOW_TICKS = 30;
    private static final int PARTICLE_COUNT = 3;

    private final FishHook hook;
    private final BlightedPlayer blightedPlayer;
    private final World.Environment environment;
    private BukkitRunnable task;

    private boolean isReadyToCatch = false;

    public LavaFishingHook(FishHook hook, BlightedPlayer blightedPlayer) {
        this.hook = hook;
        this.blightedPlayer = blightedPlayer;
        this.environment = hook.getWorld().getEnvironment();

        ACTIVE_HOOKS.put(hook.getUniqueId(), this);
        startLavaTask(60 + ThreadLocalRandom.current().nextInt(40));
    }

    private void startLavaTask(int ticksUntilCatch) {
        task = new BukkitRunnable() {
            private int timer = ticksUntilCatch;

            @Override
            public void run() {
                if (hook.isDead() || !blightedPlayer.getPlayer().isOnline()) {
                    remove();
                    return;
                }

                preventBurning();

                if (hook.getLocation().getBlock().getType() != Material.LAVA) {
                    hook.setGravity(true);
                    return;
                }

                adjustHookFloat(FLOAT_TARGET_HIGH);

                if (--timer <= 0) {
                    startCatchAnimation();
                    this.cancel();
                }
            }
        };
        task.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
    }

    private void startCatchAnimation() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double angle = random.nextDouble() * 2 * Math.PI;
        Location particleLoc = hook.getLocation().clone().add(
            Math.cos(angle) * 4.0,
            0,
            Math.sin(angle) * 4.0
        );

        task = new BukkitRunnable() {
            private boolean reachedHook = false;
            private int readyTicks = BITE_WINDOW_TICKS;

            @Override
            public void run() {
                if (hook.isDead()) {
                    remove();
                    return;
                }

                preventBurning();

                if (!reachedHook) {
                    moveParticleToHook(particleLoc);
                    adjustHookFloat(FLOAT_TARGET_HIGH);

                    if (particleLoc.distanceSquared(hook.getLocation()) < PARTICLE_DISTANCE_THRESHOLD) {
                        reachedHook = true;
                        hook.getWorld().playSound(hook.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1f, 0.5f);
                    }
                } else {
                    isReadyToCatch = true;
                    adjustHookFloat(FLOAT_TARGET_LOW);

                    if (--readyTicks <= 0) {
                        remove();
                    }
                }
            }
        };
        task.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
    }

    private void preventBurning() {
        hook.setVisualFire(false);
        hook.setFireTicks(0);
    }

    private void moveParticleToHook(Location particleLoc) {
        Vector direction = hook.getLocation().toVector()
            .subtract(particleLoc.toVector())
            .normalize()
            .multiply(PARTICLE_SPEED);

        particleLoc.add(direction);
        Objects.requireNonNull(particleLoc.getWorld()).spawnParticle(
            Particle.SMOKE,
            particleLoc,
            PARTICLE_COUNT,
            0.1, 0.1, 0.1,
            0.01
        );
    }

    private void adjustHookFloat(double targetOffset) {
        hook.setGravity(false);
        double currentOffset = hook.getLocation().getY() - hook.getLocation().getBlockY();

        if (currentOffset < targetOffset) {
            hook.setVelocity(new Vector(0, 0.04, 0));
        } else {
            hook.setVelocity(new Vector(0, -0.01, 0));
        }
    }

    public void reelIn() {
        remove();

        if (!isReadyToCatch) return;

        Player player = blightedPlayer.getPlayer();
        Location hookLoc = hook.getLocation().add(0, 0.5, 0);
        World world = hookLoc.getWorld();

        Vector velocity = player.getEyeLocation().toVector()
            .subtract(hookLoc.toVector())
            .normalize()
            .multiply(0.8)
            .setY(0.5);

        Objects.requireNonNull(world).spawnParticle(Particle.LAVA, hookLoc, 10, 0.3, 0.3, 0.3, 0.1);
        world.playSound(hookLoc, Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 1.0f);

        FishingLootTable lootTable = getLootTableForEnvironment();

        LivingEntity entity = lootTable.rollEntity(blightedPlayer, hookLoc, velocity);
        if (entity != null) return;

        ItemStack drop = lootTable.rollItem(blightedPlayer);
        if (drop != null) {
            Entity droppedItem = world.dropItem(hookLoc, drop);
            droppedItem.setVelocity(velocity);
            player.giveExp(ThreadLocalRandom.current().nextInt(3, 8));
        }
    }

    private FishingLootTable getLootTableForEnvironment() {
        return FishingLootRegistry.getTable(environment, FishingMethod.LAVA);
    }

    public void remove() {
        ACTIVE_HOOKS.remove(hook.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    public static LavaFishingHook get(FishHook hook) {
        return ACTIVE_HOOKS.get(hook.getUniqueId());
    }
}
