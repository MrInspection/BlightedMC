package fr.moussax.blightedMC.smp.core.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.FishingMethod;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FishHook;
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
    private static final double PARTICLE_DISTANCE_SQUARED = 0.25 * 0.25;

    private static final Vector FLOAT_UP = new Vector(0, 0.04, 0);
    private static final Vector FLOAT_DOWN = new Vector(0, -0.01, 0);

    private static final int BASE_WAIT_TIME = 160;     // 8 Seconds
    private static final int WAIT_TIME_VARIANCE = 240; // +0 to 12 Seconds variance
    private static final int BASE_BITE_WINDOW_TICKS = 30;

    private static final int PARTICLE_COUNT = 16; // Player feedback

    private final FishHook hook;
    private final BlightedPlayer blightedPlayer;
    private final Player player;
    private final World.Environment environment;
    private final int luckOfSeaLevel;
    private final int lureLevel;

    private BukkitRunnable task;
    private boolean isReadyToCatch = false;

    public LavaFishingHook(FishHook hook, BlightedPlayer blightedPlayer, Player player, ItemStack rod) {
        this.hook = hook;
        this.blightedPlayer = blightedPlayer;
        this.player = player;
        this.environment = hook.getWorld().getEnvironment();

        if (rod != null) {
            this.luckOfSeaLevel = rod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
            this.lureLevel = rod.getEnchantmentLevel(Enchantment.LURE);
        } else {
            this.luckOfSeaLevel = 0;
            this.lureLevel = 0;
        }

        ACTIVE_HOOKS.put(hook.getUniqueId(), this);
        startLavaTask(calculateWaitTime());
    }

    private int calculateWaitTime() {
        int baseTime = Math.max(40, BASE_WAIT_TIME - (lureLevel * 40));
        return baseTime + ThreadLocalRandom.current().nextInt(WAIT_TIME_VARIANCE);
    }

    private int calculateBiteWindow() {
        return BASE_BITE_WINDOW_TICKS + (luckOfSeaLevel * 10);
    }

    private void startLavaTask(int ticksUntilCatch) {
        task = new BukkitRunnable() {
            private int timer = ticksUntilCatch;

            @Override
            public void run() {
                if (isInvalid()) return;

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

        Location particleLocation = hook.getLocation().clone().add(
            Math.cos(angle) * 4.0,
            0,
            Math.sin(angle) * 4.0
        );

        task = new BukkitRunnable() {
            private boolean reachedHook = false;
            private int readyTicks = calculateBiteWindow();

            @Override
            public void run() {
                if (isInvalid()) return;

                if (!reachedHook) {
                    moveParticleToHook(particleLocation);
                    adjustHookFloat(FLOAT_TARGET_HIGH);

                    if (particleLocation.distanceSquared(hook.getLocation()) < PARTICLE_DISTANCE_SQUARED) {
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

    private boolean isInvalid() {
        if (hook.isDead() || !player.isOnline()) {
            remove();
            return true;
        }

        hook.setVisualFire(false);
        hook.setFireTicks(0);

        if (hook.isInWater()) {
            hook.remove();
            remove();
            Formatter.warn(player, "Lava fishing rods cannot be used in water!");
            return true;
        }
        return false;
    }

    private void moveParticleToHook(Location particleLoc) {
        Vector direction = hook.getLocation().toVector().subtract(particleLoc.toVector());
        direction.normalize().multiply(PARTICLE_SPEED);
        particleLoc.add(direction);

        Objects.requireNonNull(particleLoc.getWorld()).spawnParticle(
            Particle.SMOKE, particleLoc, PARTICLE_COUNT, 0.05, 0.05, 0.05, 0.01
        );
    }

    private void adjustHookFloat(double targetOffset) {
        hook.setGravity(false);
        double currentOffset = hook.getLocation().getY() - hook.getLocation().getBlockY();

        hook.setVelocity(currentOffset < targetOffset ? FLOAT_UP : FLOAT_DOWN);
    }

    public boolean reelIn() {
        remove();

        if (!isReadyToCatch) return false;

        Location hookLocation = hook.getLocation();
        World world = hookLocation.getWorld();
        if (world == null) return false;

        world.spawnParticle(Particle.LAVA, hookLocation, 10, 0.3, 0.3, 0.3, 0.1);
        world.playSound(hookLocation, Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 1.0f);

        Location playerLocation = player.getLocation();
        Vector velocity = playerLocation.toVector().subtract(hookLocation.toVector());

        double distance = velocity.length();
        velocity.multiply(0.08);
        velocity.setY(velocity.getY() + (Math.sqrt(distance) * 0.05) + 0.15);

        Location spawnLocation = hookLocation.add(0, 0.5, 0);

        FishingLootTable lootTable = FishingLootRegistry.getTable(environment, FishingMethod.LAVA);
        boolean success = lootTable.roll(blightedPlayer, spawnLocation, velocity);

        if (success) {
            ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(playerLocation, EntityType.EXPERIENCE_ORB);
            orb.setExperience(ThreadLocalRandom.current().nextInt(3, 8));
        }

        return success;
    }

    public void remove() {
        ACTIVE_HOOKS.remove(hook.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
    }

    public static LavaFishingHook get(FishHook hook) {
        return ACTIVE_HOOKS.get(hook.getUniqueId());
    }
}
