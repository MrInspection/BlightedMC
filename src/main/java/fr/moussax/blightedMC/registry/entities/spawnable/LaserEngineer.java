package fr.moussax.blightedMC.registry.entities.spawnable;

import fr.moussax.blightedMC.core.entities.EntityAttributes;
import fr.moussax.blightedMC.core.entities.LootTable.LootDropRarity;
import fr.moussax.blightedMC.core.entities.LootTable.LootTable;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.core.entities.spawning.condition.BiomeCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.ChanceCondition;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@EntityAttributes(EntityAttributes.Attributes.PROJECTILE_IMMUNITY)
public class LaserEngineer extends SpawnableEntity {
  private BukkitRunnable laserAbilityTask;
  private boolean isAttacking = false;
  private int attackTicksRemaining = 0;
  private Player attackTarget = null;

  private static final double LASER_RANGE = 16.0;
  private static final double LASER_DAMAGE = 1.5;
  private static final long TICK_DELAY = 10L;

  public LaserEngineer() {
    super("LASER_ENGINEER", "Laser Engineer", 28, EntityType.ZOMBIE, 0.08);
    setDamage(10);
    setDefense(10);
    setDroppedExp(6);

    setLootTable(new LootTable().setMaxDrop(2)
      .addLoot(Material.ROTTEN_FLESH, 1, 3, 0.90, LootDropRarity.COMMON)
      .addFavorsLoot(5, 0.03, LootDropRarity.EXTRAORDINARY)
    );

    armor = new ItemStack[]{
      new ItemBuilder(Material.LEATHER_BOOTS).setLeatherColor("#737775").addEnchantmentGlint().setUnbreakable(true).toItemStack(),
      new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherColor("#7D807E").setUnbreakable(true).toItemStack(),
      new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor("#7D807E").setUnbreakable(true).toItemStack(),
      new ItemBuilder(Material.OBSERVER).toItemStack()
    };

    addRepeatingTask(() -> {
      BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
          LivingEntity self = getEntity();
          if (self == null || !self.isValid()) {
            cancel();
            return;
          }

          List<Player> nearbyPlayers = self.getNearbyEntities(LASER_RANGE, LASER_RANGE, LASER_RANGE)
            .stream()
            .filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
            .map(e -> (Player) e)
            .toList();

          if (nearbyPlayers.isEmpty()) {
            if (isAttacking) {
              isAttacking = false;
              switchArmorMode(false);
            }
            return;
          }

          // If in attack mode
          if (isAttacking) {
            if (attackTarget == null || !attackTarget.isOnline() || attackTarget.getWorld() != self.getWorld()
              || attackTarget.getLocation().distance(self.getLocation()) > LASER_RANGE) {
              // Lost target
              isAttacking = false;
              attackTicksRemaining = 0;
              switchArmorMode(false);
              return;
            }

            startLaserFocus(self, BlightedPlayer.getBlightedPlayer(attackTarget));
            if (--attackTicksRemaining <= 0) {
              isAttacking = false;
              switchArmorMode(false);
            }
            return;
          }

          // Idle → roll to start attack
          if (ThreadLocalRandom.current().nextDouble() < 0.3) { // 30% chance per cycle
            attackTarget = nearbyPlayers.get(ThreadLocalRandom.current().nextInt(nearbyPlayers.size()));
            isAttacking = true;
            attackTicksRemaining = ThreadLocalRandom.current().nextInt(40, 61); // 2–3 seconds
            switchArmorMode(true);
          }
        }
      };

      laserAbilityTask = task;
      return task;
    }, 100L, TICK_DELAY);
  }

  private void switchArmorMode(boolean isActive) {
    if (!(entity instanceof Zombie zombie)) return;

    if (isActive) {
      Objects.requireNonNull(zombie.getEquipment()).setChestplate(
        new ItemBuilder(Material.LEATHER_CHESTPLATE)
          .setLeatherColor("#7D807E")
          .setArmorTrim(TrimMaterial.RESIN, TrimPattern.SNOUT)
          .addEnchantmentGlint()
          .setUnbreakable(true)
          .toItemStack()
      );
      zombie.getEquipment().setHelmet(
        new ItemBuilder(Material.PLAYER_HEAD)
          .setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzhiYjdjZDFkMThjNWI2MWU5ZjA4OTBhZjQ5MjllZTdjN2FiOTUyZWUyYjZlYWJlMzM3ZTlmMGI0ZWM4MzVkNCJ9fX0=")
          .toItemStack()
      );
      return;
    }

    Objects.requireNonNull(zombie.getEquipment()).setHelmet(
      new ItemBuilder(Material.OBSERVER).toItemStack()
    );
    zombie.getEquipment().setChestplate(
      new ItemBuilder(Material.LEATHER_CHESTPLATE)
        .setLeatherColor("#7D807E")
        .addEnchantmentGlint()
        .setUnbreakable(true)
        .toItemStack()
    );
  }

  private void startLaserFocus(LivingEntity origin, BlightedPlayer target) {
    Player player = target.getPlayer();
    if (player == null || !player.isOnline()) return;

    Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.5f);

    // Face target
    Vector direction = player.getLocation().toVector().subtract(entity.getEyeLocation().toVector()).normalize();
    Location mobLocation = entity.getLocation();
    mobLocation.setDirection(direction.clone());
    entity.teleport(mobLocation);

    // Damage target each tick
    player.damage(LASER_DAMAGE, origin);

    // Draw beam
    Vector step = direction.multiply(0.2);
    Location current = entity.getEyeLocation().clone();
    int safety = 0;
    while (player.getLocation().distance(current) > 0.5) {
      current.add(step);
      Objects.requireNonNull(current.getWorld()).spawnParticle(Particle.DUST, current, 1, dust);
      if (safety++ > 200) break;
    }
  }

  @Override
  public LivingEntity spawn(Location location) {
    super.spawn(location);
    return entity;
  }

  @Override
  public void kill() {
    super.kill();
  }

  @Override
  public SpawnableEntity clone() {
    LaserEngineer clone = (LaserEngineer) super.clone();
    clone.laserAbilityTask = null;
    clone.isAttacking = false;
    clone.attackTicksRemaining = 0;
    clone.attackTarget = null;

    clone.addRepeatingTask(() -> {
      BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
          LivingEntity self = clone.getEntity();
          if (self == null || !self.isValid()) {
            cancel();
            return;
          }

          List<Player> nearbyPlayers = self.getNearbyEntities(LASER_RANGE, LASER_RANGE, LASER_RANGE)
            .stream()
            .filter(e -> e instanceof Player p && p.getGameMode() == GameMode.SURVIVAL)
            .map(e -> (Player) e)
            .toList();

          if (nearbyPlayers.isEmpty()) {
            if (clone.isAttacking) {
              clone.isAttacking = false;
              clone.switchArmorMode(false);
            }
            return;
          }

          if (clone.isAttacking) {
            if (clone.attackTarget == null || !clone.attackTarget.isOnline() ||
              clone.attackTarget.getWorld() != self.getWorld() ||
              clone.attackTarget.getLocation().distance(self.getLocation()) > LASER_RANGE) {
              clone.isAttacking = false;
              clone.attackTicksRemaining = 0;
              clone.switchArmorMode(false);
              return;
            }
            clone.startLaserFocus(self, BlightedPlayer.getBlightedPlayer(clone.attackTarget));
            if (--clone.attackTicksRemaining <= 0) {
              clone.isAttacking = false;
              clone.switchArmorMode(false);
            }
            return;
          }

          if (ThreadLocalRandom.current().nextDouble() < 0.3) {
            clone.attackTarget = nearbyPlayers.get(ThreadLocalRandom.current().nextInt(nearbyPlayers.size()));
            clone.isAttacking = true;
            clone.attackTicksRemaining = ThreadLocalRandom.current().nextInt(40, 61);
            clone.switchArmorMode(true);
          }
        }
      };
      clone.laserAbilityTask = task;
      return task;
    }, TICK_DELAY, TICK_DELAY);

    return clone;
  }

  @Override
  protected void setupSpawnConditions() {
    addSpawnCondition(new BiomeCondition(Set.of(
      Biome.FOREST,
      Biome.BIRCH_FOREST,
      Biome.DARK_FOREST,
      Biome.FLOWER_FOREST
    )));
    addSpawnCondition(new ChanceCondition(0.2));
  }
}
