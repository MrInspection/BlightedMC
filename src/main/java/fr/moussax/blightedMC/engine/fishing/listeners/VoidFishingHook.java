package fr.moussax.blightedMC.engine.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import fr.moussax.blightedMC.engine.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.Formatter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class VoidFishingHook {
    private static final Map<UUID, VoidFishingHook> ACTIVE_HOOKS = new HashMap<>();

    private static final double CENTRAL_ISLAND_RADIUS_SQUARED = 1000.0 * 1000.0;
    private static final double MIN_DROP_DISTANCE = 20.0;
    private static final double MIN_Y_LEVEL = 0.0;

    private static final int BASE_MIN_WAIT_TICKS = 300;
    private static final int BASE_MAX_WAIT_TICKS = 900;
    private static final int LURE_MAX_DEDUCTION = 100;

    private static final int BASE_BITE_WINDOW_TICKS = 40;
    private static final double PARTICLE_SPEED = 0.2;
    private static final double PARTICLE_DISTANCE_SQUARED = 0.25 * 0.25;

    private static final Vector FLOAT_UP = new Vector(0, 0.02, 0);
    private static final Vector FLOAT_DOWN = new Vector(0, -0.01, 0);

    private final FishHook hook;
    private final BlightedPlayer blightedPlayer;
    private final Player player;
    private final World.Environment environment;
    private final int luckOfSeaLevel;
    private final int lureLevel;
    private final double speedMultiplier;

    private BukkitRunnable task;
    private boolean isReadyToCatch = false;
    private boolean suspendedInVoid = false;
    private double targetY;
    private double particleAngleOffset = 0.0;

    public VoidFishingHook(FishHook hook, BlightedPlayer blightedPlayer, Player player, ItemStack rod, double speedMultiplier) {
        this.hook = hook;
        this.blightedPlayer = blightedPlayer;
        this.player = player;
        this.environment = hook.getWorld().getEnvironment();
        this.speedMultiplier = speedMultiplier;

        if (rod != null) {
            this.luckOfSeaLevel = rod.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA);
            this.lureLevel = rod.getEnchantmentLevel(Enchantment.LURE);
        } else {
            this.luckOfSeaLevel = 0;
            this.lureLevel = 0;
        }

        ACTIVE_HOOKS.put(hook.getUniqueId(), this);
        startVoidDropTask();
    }

    private void startVoidDropTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (isInvalid()) return;
                Location hookLoc = hook.getLocation();

                if (hookLoc.getY() <= MIN_Y_LEVEL) {
                    Formatter.warn(player, "The void consumes your hook. Life cannot endure beneath Y0.");
                    remove();
                    hook.remove();
                    return;
                }

                double dropDistance = player.getLocation().getY() - hookLoc.getY();
                if (dropDistance >= MIN_DROP_DISTANCE) {
                    double distanceSquared = (hookLoc.getX() * hookLoc.getX()) + (hookLoc.getZ() * hookLoc.getZ());
                    if (distanceSquared <= CENTRAL_ISLAND_RADIUS_SQUARED) {
                        Formatter.warn(player, "The void beneath the central island is barren. Cast farther out.");
                        remove();
                        hook.remove();
                        return;
                    }

                    suspendedInVoid = true;
                    targetY = hookLoc.getY();

                    startWaitTask(calculateWaitTime());
                    this.cancel();
                }
            }
        };
        task.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
    }

    private int calculateWaitTime() {
        int maxTicks = BASE_MAX_WAIT_TICKS - (lureLevel * LURE_MAX_DEDUCTION);
        maxTicks = Math.max(BASE_MIN_WAIT_TICKS, maxTicks);

        int totalWaitTime = ThreadLocalRandom.current().nextInt(BASE_MIN_WAIT_TICKS, maxTicks + 1);
        return (int) (totalWaitTime * speedMultiplier);
    }

    private int calculateBiteWindow() {
        return BASE_BITE_WINDOW_TICKS + (luckOfSeaLevel * 10);
    }

    private void startWaitTask(int ticksUntilCatch) {
        task = new BukkitRunnable() {
            private int timer = ticksUntilCatch;

            @Override
            public void run() {
                if (isInvalid()) return;
                maintainFloatPosition();
                spawnIdleParticles();

                if (--timer <= 0) {
                    startCatchAnimation();
                    this.cancel();
                }
            }
        };
        task.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
    }

    private void maintainFloatPosition() {
        hook.setGravity(false);
        double currentY = hook.getLocation().getY();
        hook.setVelocity(currentY < targetY ? FLOAT_UP : FLOAT_DOWN);
    }

    private void spawnIdleParticles() {
        World world = hook.getWorld();

        particleAngleOffset += 0.15;
        if (particleAngleOffset > Math.PI * 2) {
            particleAngleOffset -= Math.PI * 2;
        }

        Location center = hook.getLocation();
        double radius = 1.2;

        double x1 = Math.cos(particleAngleOffset) * radius;
        double z1 = Math.sin(particleAngleOffset) * radius;
        world.spawnParticle(Particle.PORTAL, center.clone().add(x1, 0, z1), 0, 0, 0, 0, 0);

        double x2 = Math.cos(particleAngleOffset + Math.PI) * radius;
        double z2 = Math.sin(particleAngleOffset + Math.PI) * radius;
        world.spawnParticle(Particle.REVERSE_PORTAL, center.clone().add(x2, 0, z2), 0, 0, 0, 0, 0);
    }

    private void startCatchAnimation() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double angle = random.nextDouble() * 2 * Math.PI;

        Location particleLocation = hook.getLocation().clone().add(Math.cos(angle) * 5.0, 0, Math.sin(angle) * 5.0);

        task = new BukkitRunnable() {
            private boolean reachedHook = false;
            private int readyTicks = calculateBiteWindow();

            @Override
            public void run() {
                if (isInvalid()) return;

                if (!reachedHook) {
                    maintainFloatPosition();
                    moveParticleToHook(particleLocation);

                    if (particleLocation.distanceSquared(hook.getLocation()) < PARTICLE_DISTANCE_SQUARED) {
                        reachedHook = true;
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1.0f, 0.5f);
                        Objects.requireNonNull(hook.getWorld()).spawnParticle(Particle.WITCH, hook.getLocation(), 30, 0.3, 0.5, 0.3, 0.1);
                        hook.setVelocity(new Vector(0, -0.2, 0));
                    }
                } else {
                    isReadyToCatch = true;
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
        return false;
    }

    private void moveParticleToHook(Location particleLoc) {
        Vector direction = hook.getLocation().toVector().subtract(particleLoc.toVector());
        direction.normalize().multiply(PARTICLE_SPEED);
        particleLoc.add(direction);

        Objects.requireNonNull(particleLoc.getWorld()).spawnParticle(
                Particle.END_ROD, particleLoc, 3, 0.02, 0.02, 0.02, 0.0
        );
    }

    public boolean reelIn() {
        remove();

        if (!isReadyToCatch || !suspendedInVoid) return false;

        Location hookLocation = hook.getLocation();
        World world = hookLocation.getWorld();
        if (world == null) return false;

        world.spawnParticle(Particle.REVERSE_PORTAL, hookLocation, 50, 0.5, 1.0, 0.5, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);

        Location playerLocation = player.getLocation();
        Vector velocity = playerLocation.toVector().subtract(hookLocation.toVector());

        double distance = velocity.length();
        velocity.multiply(0.08);
        velocity.setY(velocity.getY() + (Math.sqrt(distance) * 0.05) + 0.15);

        Location spawnLocation = hookLocation.add(0, 0.5, 0);

        FishingLootTable lootTable = FishingLootRegistry.getTable(environment, FishingMethod.VOID);
        boolean success = lootTable.roll(blightedPlayer, spawnLocation, velocity);

        if (success) {
            ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(playerLocation, EntityType.EXPERIENCE_ORB);
            orb.setExperience(ThreadLocalRandom.current().nextInt(5, 12));
        }

        return success;
    }

    public void remove() {
        ACTIVE_HOOKS.remove(hook.getUniqueId());
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
        if (!hook.isDead()) {
            hook.setGravity(true);
        }
    }

    public static VoidFishingHook get(FishHook hook) {
        return ACTIVE_HOOKS.get(hook.getUniqueId());
    }

    public static void cleanupAll() {
        new ArrayList<>(ACTIVE_HOOKS.values()).forEach(VoidFishingHook::remove);
    }
}
