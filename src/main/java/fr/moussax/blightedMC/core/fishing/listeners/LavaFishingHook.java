package fr.moussax.blightedMC.core.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.core.fishing.LootTable.pools.LavaFishingPool;
import fr.moussax.blightedMC.core.fishing.LootTable.pools.NetherFishingPool;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class LavaFishingHook {
  private static final HashMap<FishHook, LavaFishingHook> activeHooks = new HashMap<>();
  private static final Random RANDOM = new Random();

  private final FishHook fishingHook;
  private final BlightedPlayer fishingPlayer;
  private BukkitRunnable fishingTask;
  private boolean isReadyToCatch = false;

  public LavaFishingHook(FishHook hook, BlightedPlayer player, int ticksUntilCatch) {
    this.fishingHook = hook;
    this.fishingPlayer = player;
    startLavaCheckTask(ticksUntilCatch);
    activeHooks.put(fishingHook, this);
  }

  private void startLavaCheckTask(int ticksUntilCatch) {
    fishingTask = new BukkitRunnable() {
      private int remainingTicks = ticksUntilCatch;
      private boolean wasInLava = false;

      @Override
      public void run() {
        resetHookFire();

        Material blockType = fishingHook.getLocation().getBlock().getType();
        if (blockType != Material.LAVA) {
          if (wasInLava) remainingTicks--;
          return;
        }

        adjustHookFloat();
        remainingTicks--;
        wasInLava = true;

        if (remainingTicks <= 0) startCatchAnimation();
        if (fishingHook.isDead()) cancel();
      }
    };
    fishingTask.runTaskTimer(BlightedMC.getInstance(), 0L, 1L);
  }

  private void adjustHookFloat() {
    double offset = fishingHook.getLocation().getY() - fishingHook.getLocation().getBlockY();
    if (offset < 0.85) fishingHook.setVelocity(new Vector(0, 0.05, 0));
    fishingHook.setGravity(false);
  }

  private void resetHookFire() {
    fishingHook.setVisualFire(false);
    fishingHook.setFireTicks(0);
  }

  public void remove() {
    activeHooks.remove(fishingHook);
    if (fishingTask != null) fishingTask.cancel();
  }

  private void startCatchAnimation() {
    fishingTask.cancel();

    double randomX = (RANDOM.nextDouble() * 2 - 1) * 4;
    double randomZ = (RANDOM.nextDouble() * 2 - 1) * 4;

    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0x2d2d2e), 2);

    fishingTask = new BukkitRunnable() {
      private boolean reachedHook = false;
      private Location particleLocation = fishingHook.getLocation().clone().add(randomX, 0, randomZ);
      private int readyTicks = 20;

      @Override
      public void run() {
        resetHookFire();

        if(fishingHook.getLocation().getBlock().getType() != Material.LAVA) return;

        if(!reachedHook) {
          Vector direction = fishingHook.getLocation().toVector().subtract(particleLocation.toVector()).normalize().multiply(0.1);
          particleLocation.add(direction);
          fishingHook.getWorld().spawnParticle(Particle.SMOKE, particleLocation, 1);
          if(fishingHook.getLocation().distance(particleLocation) <= 0.3) reachedHook = true;
        } else {
          isReadyToCatch = readyTicks > 0;
          readyTicks--;
        }

        adjustCatchFloat();
        if(fishingHook.isDead()) cancel();
      }
    };
    fishingTask.runTaskTimer(BlightedMC.getInstance(), 0L, 1L);
  }

  private void adjustCatchFloat() {
    double offset = fishingHook.getLocation().getY() - fishingHook.getLocation().getBlockY();
    if((!isReadyToCatch && offset < 0.85) || (isReadyToCatch && offset < 0.6))
      fishingHook.setVelocity(new Vector(0, 0.05, 0));
  }

  public void reelIn() {
    remove();

    Location hookLocation = fishingHook.getLocation().add(0,0.5,0);
    Vector velocityToPlayer = fishingPlayer.getPlayer().getEyeLocation().toVector().subtract(hookLocation.toVector()).multiply(0.15);

    // Lava splash particle & sound
    World world = fishingHook.getWorld();
    world.spawnParticle(Particle.LAVA, hookLocation, 10, 0.3, 0.3, 0.3, 0.01);
    world.playSound(hookLocation, Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 1.0f);

    double seaCreatureChance = 0.25;
    World.Environment env = fishingPlayer.getPlayer().getWorld().getEnvironment();
    if (RANDOM.nextDouble() < seaCreatureChance) {
      if (env == World.Environment.NETHER) new NetherFishingPool().summonSeaCreature(fishingPlayer, hookLocation, velocityToPlayer);
      else new LavaFishingPool().summonSeaCreature(fishingPlayer, hookLocation, velocityToPlayer);
      return;
    }

    ItemStack drop;
    if (env == World.Environment.NETHER) drop = new NetherFishingPool().getItemDrop(fishingPlayer);
    else drop = new LavaFishingPool().getItemDrop(fishingPlayer);

    if (drop != null && drop.getType() != Material.AIR) {
      Entity droppedItem = Objects.requireNonNull(hookLocation.getWorld()).dropItemNaturally(hookLocation, drop);
      droppedItem.setVelocity(velocityToPlayer);
    }
  }

  public static LavaFishingHook getHook(FishHook hook) { return activeHooks.get(hook); }
  public static boolean contains(FishHook hook) { return activeHooks.containsKey(hook); }
}
