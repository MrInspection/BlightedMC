package fr.moussax.blightedMC.core.registry.entities.spawnable;

import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Boulder extends SpawnableEntity {
  private BukkitRunnable yeetAbilityTask;

  private boolean isYeeting = false;
  private int yeetTick = 0;
  private final List<ArmorStand> yeetBlocks = new ArrayList<>();
  private Location yeetProjectileLocation;
  private Vector yeetProjectileDirection;
  private double yeetSpeedMultiplier;
  private double yeetBeforeY = 0.0;
  private int yeetDuration = 20;
  private Player yeetTarget;

  private static final long YEET_INITIAL_DELAY = 40L;
  private static final long YEET_PERIOD = 1L;
  private static final double YEET_RANGE = 20.0;
  private static final double YEET_DAMAGE = 12.0;
  private static final double YEET_BLOCK_TRAVEL_FACTOR = 0.12; // travel factor multiplied by distance
  private static final double YEET_IMPACT_RADIUS = 7.0;
  private static final double YEET_HIT_RADIUS = 1.6; // collision distance to register a hit

  public Boulder() {
    super("BOULDER", "The Boulder", 28, EntityType.STRAY, 0.05);

    setDamage(10);
    setDefense(10);
    setDroppedExp(6);

    armor = new ItemStack[]{
      new ItemBuilder(Material.LEATHER_BOOTS).setLeatherColor("#81C1E1").setUnbreakable(true).toItemStack(),
      new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherColor("#70B8DD").setUnbreakable(true).toItemStack(),
      new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor("#70B8DD").setUnbreakable(true).toItemStack(),
      new ItemStack(Material.CYAN_STAINED_GLASS)
    };

    addRepeatingTask(() -> {
      BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
          LivingEntity self = getEntity();
          if (self == null || !self.isValid()) {
            cancel();
            cleanupYeetBlocks();
            return;
          }

          if (isYeeting) {
            advanceYeetSequence(self);
            return;
          }

          List<Player> nearbyPlayers = self.getNearbyEntities(YEET_RANGE, YEET_RANGE, YEET_RANGE)
            .stream().filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
            .map(e -> (Player) e)
            .toList();

          if (nearbyPlayers.isEmpty()) return;

          if (ThreadLocalRandom.current().nextDouble() < 0.08) {
            Player chosen = nearbyPlayers.get(ThreadLocalRandom.current().nextInt(nearbyPlayers.size()));
            startYeet(self, chosen);
          }
        }
      };
      yeetAbilityTask = task;
      return task;
    }, YEET_INITIAL_DELAY, YEET_PERIOD);
  }

  private void startYeet(LivingEntity origin, Player target) {
    if (origin == null || !origin.isValid() || target == null || !target.isOnline()) return;

    isYeeting = true;
    yeetTick = 0;
    yeetTarget = target;
    yeetProjectileLocation = origin.getLocation().clone().add(0, 1.2, 0);
    yeetProjectileDirection = target.getLocation().toVector().subtract(yeetProjectileLocation.toVector()).normalize();
    double distance = Math.max(1.0, yeetProjectileLocation.distance(target.getLocation()));
    yeetSpeedMultiplier = YEET_BLOCK_TRAVEL_FACTOR * distance;
    yeetDuration = Math.max(20, (int) (distance * 0.9)); // scale duration with distance
    yeetBeforeY = getYPos(0);
    spawnYeetBlocks(yeetProjectileLocation);
  }

  private void advanceYeetSequence(LivingEntity origin) {
    if (!isYeeting) return;

    // safety: if target lost, cancel yeet
    if (yeetTarget == null || !yeetTarget.isOnline() || yeetTarget.getWorld() != origin.getWorld()) {
      cleanupYeetBlocks();
      isYeeting = false;
      yeetTarget = null;
      yeetTick = 0;
      return;
    }

    yeetTick++;

    double currentYPos = getYPos(yeetTick * 0.1 * Math.PI);
    double deltaY = currentYPos - yeetBeforeY;

    // move blocks forward and apply delta Y
    for (ArmorStand block : yeetBlocks) {
      Location updated = block.getLocation().add(yeetProjectileDirection.clone().multiply(yeetSpeedMultiplier)).add(0, deltaY, 0);
      block.teleport(updated);
    }

    // advance projectile location with same delta
    yeetProjectileLocation = yeetProjectileLocation.add(yeetProjectileDirection.clone().multiply(yeetSpeedMultiplier)).add(0, deltaY, 0);
    yeetBeforeY = currentYPos;

    // --- collision check with targeted player ---
    double distanceToTarget = yeetProjectileLocation.distance(yeetTarget.getLocation());
    if (distanceToTarget <= YEET_HIT_RADIUS) {
      origin.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, yeetProjectileLocation, 2);
      yeetTarget.damage(YEET_DAMAGE, origin);

      Vector knockback = yeetTarget.getLocation().toVector().subtract(origin.getLocation().toVector()).normalize().multiply(0.35);
      knockback.setY(0.28);
      yeetTarget.setVelocity(knockback);

      damagePlayersAroundImpact(origin, yeetProjectileLocation, 2.5, YEET_DAMAGE * 0.6);

      cleanupYeetBlocks();
      isYeeting = false;
      yeetTarget = null;
      yeetTick = 0;
      return;
    }

    if (yeetTick >= yeetDuration) {
      origin.getWorld().spawnParticle(Particle.EXPLOSION, yeetProjectileLocation, 2);
      damagePlayersAroundImpact(origin, yeetProjectileLocation, YEET_IMPACT_RADIUS, YEET_DAMAGE);
      cleanupYeetBlocks();
      isYeeting = false;
      yeetTarget = null;
      yeetTick = 0;
    }
  }

  private double getYPos(double t) {
    return 1.2 * Math.sin(t * 0.9) + 0.25; // smooth gentle vertical
  }

  private void spawnYeetBlocks(Location center) {
    cleanupYeetBlocks();

    yeetBlocks.add(spawnInvisibleArmorStand(center.clone(), Material.BLUE_ICE));
    yeetBlocks.add(spawnInvisibleArmorStand(center.clone().add(0.625, 0, 0), Material.BLUE_ICE));
    yeetBlocks.add(spawnInvisibleArmorStand(center.clone().add(-0.625, 0, 0), Material.BLUE_ICE));
    yeetBlocks.add(spawnInvisibleArmorStand(center.clone().add(0, 0, 0.625), Material.BLUE_ICE));
    yeetBlocks.add(spawnInvisibleArmorStand(center.clone().add(0, 0, -0.625), Material.BLUE_ICE));
    yeetBlocks.add(spawnInvisibleArmorStand(center.clone().add(0.625, 0, -0.625), Material.BLUE_ICE));
    yeetBlocks.add(spawnInvisibleArmorStand(center.clone().add(-0.625, 0, -0.625), Material.BLUE_ICE));
    yeetBlocks.add(spawnInvisibleArmorStand(center.clone().add(0.625, 0, 0.625), Material.BLUE_ICE));
    yeetBlocks.add(spawnInvisibleArmorStand(center.clone().add(-0.625, 0, 0.625), Material.BLUE_ICE));
  }

  private ArmorStand spawnInvisibleArmorStand(Location location, Material helmetMaterial) {
    return Objects.requireNonNull(location.getWorld()).spawn(location, ArmorStand.class, stand -> {
      stand.setVisible(false);
      stand.setInvulnerable(true);
      stand.setGravity(false);
      Objects.requireNonNull(stand.getEquipment()).setHelmet(new ItemStack(helmetMaterial));
      stand.setMarker(true);
    });
  }

  private void damagePlayersAroundImpact(LivingEntity origin, Location center, double radius, double damage) {
    for (Entity e : Objects.requireNonNull(center.getWorld()).getNearbyEntities(center, radius, radius, radius)) {
      if (!(e instanceof Player player)) continue;
      player.damage(damage, origin);
      Vector knockback = player.getLocation().toVector().subtract(origin.getLocation().toVector()).normalize().multiply(0.35);
      knockback.setY(0.28);
      player.setVelocity(knockback);
    }
  }

  private void cleanupYeetBlocks() {
    for (ArmorStand block : yeetBlocks) {
      if (block != null && !block.isDead()) block.remove();
    }
    yeetBlocks.clear();
  }

  @Override
  public SpawnableEntity clone() {
    Boulder clone = (Boulder) super.clone();

    clone.yeetAbilityTask = null;
    clone.isYeeting = false;
    clone.yeetTick = 0;
    clone.yeetBlocks.clear();
    clone.yeetProjectileLocation = null;
    clone.yeetProjectileDirection = null;
    clone.yeetSpeedMultiplier = 0.0;
    clone.yeetBeforeY = 0.0;
    clone.yeetDuration = 20;
    clone.yeetTarget = null;

    clone.addRepeatingTask(() -> {
      BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
          LivingEntity self = clone.getEntity();
          if (self == null || !self.isValid()) {
            cancel();
            clone.cleanupYeetBlocks();
            return;
          }

          if (clone.isYeeting) {
            clone.advanceYeetSequence(self);
            return;
          }

          List<Player> nearbyPlayers = self.getNearbyEntities(YEET_RANGE, YEET_RANGE, YEET_RANGE)
            .stream().filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
            .map(e -> (Player) e)
            .toList();

          if (nearbyPlayers.isEmpty()) return;

          if (ThreadLocalRandom.current().nextDouble() < 0.08) {
            Player chosen = nearbyPlayers.get(ThreadLocalRandom.current().nextInt(nearbyPlayers.size()));
            clone.startYeet(self, chosen);
          }
        }
      };
      clone.yeetAbilityTask = task;
      return task;
    }, YEET_INITIAL_DELAY, YEET_PERIOD);

    return clone;
  }

  @Override
  protected void setupSpawnConditions() {
    // no specific conditions
  }
}
