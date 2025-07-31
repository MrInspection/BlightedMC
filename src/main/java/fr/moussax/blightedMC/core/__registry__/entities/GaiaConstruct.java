package fr.moussax.blightedMC.core.__registry__.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.core.entities.LootTable.LootTable;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class GaiaConstruct extends BlightedEntity {
  private int hits;
  private final boolean highLevel;

  public GaiaConstruct(boolean highLevel) {
    super("§6Gaia Construct", 200, EntityType.IRON_GOLEM);
    this.highLevel = highLevel;
    this.level = highLevel ? 260 : 140;
    this.hits = 6;

    setNameTagType(EntityNameTag.SMALL_NUMBER);
    addAttribute(Attribute.ATTACK_DAMAGE, 24);
  }

  @Override
  public LivingEntity spawn(Location location) {
    super.spawn(location);

    new BukkitRunnable() {
      int counter = 0;

      @Override
      public void run() {
        if (entity == null || entity.isDead()) {
          cancel();
          return;
        }

        Player target = getNearestPlayer(entity.getLocation(), 20);
        if (!(entity instanceof Mob mob)) {
          return;
        }

        if (target == null) {
          mob.setTarget(null);
          return;
        }

        mob.setTarget(target);

        counter++;
        if (counter >= getSkillDelay()) {
          counter = 0;
          performSkill(target);
        }
      }
    }.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);

    return entity;
  }

  private int getSkillDelay() {
    double current = entity.getHealth();
    if (current > maxHealth * 0.66) return 40;
    if (current > maxHealth * 0.33) return 20;
    return 8;
  }

  private void performSkill(Player player) {
    Block centerBlock = getSafeGround(player.getLocation().getBlock());
    if (centerBlock == null) return;

    LinkedList<Block> positions = new LinkedList<>();
    positions.add(centerBlock);
    positions.add(getSafeGround(centerBlock.getRelative(BlockFace.NORTH)));
    positions.add(getSafeGround(centerBlock.getRelative(BlockFace.SOUTH)));
    positions.add(getSafeGround(centerBlock.getRelative(BlockFace.EAST)));
    positions.add(getSafeGround(centerBlock.getRelative(BlockFace.WEST)));

    LinkedList<FallingBlock> visuals = new LinkedList<>();
    for (Block block : positions) {
      if (block == null) continue;
      Location spawnLoc = block.getLocation().add(0.5, 1, 0.5);
      FallingBlock fb = block.getWorld().spawnFallingBlock(spawnLoc, Material.IRON_BLOCK.createBlockData());
      fb.setGravity(false);
      fb.setDropItem(false);
      fb.setHurtEntities(false);
      fb.setInvulnerable(true);
      fb.setPersistent(true);
      fb.setSilent(true);
      fb.setGlowing(false);
      visuals.add(fb);
    }

    Location center = centerBlock.getLocation().add(0.5, 1, 0.5);
    Objects.requireNonNull(center.getWorld()).playSound(center, Sound.BLOCK_ANVIL_PLACE, 1, 1.5f);
    center.getWorld().strikeLightningEffect(center);

    // Keep falling blocks visually in place by teleporting each tick
    BukkitRunnable visualTask = new BukkitRunnable() {
      int ticks = 0;

      @Override
      public void run() {
        if (ticks++ >= 15) {
          for (FallingBlock fb : visuals) {
            if (!fb.isDead()) fb.remove();
          }
          cancel();
          return;
        }

        for (int i = 0; i < visuals.size(); i++) {
          FallingBlock fb = visuals.get(i);
          if (fb.isDead()) continue;
          Block block = positions.get(i);
          Location loc = block.getLocation().add(0.5, 1, 0.5);
          fb.teleport(loc);
          fb.setVelocity(new Vector(0, 0, 0));
        }
      }
    };
    visualTask.runTaskTimer(BlightedMC.getInstance(), 0L, 1L);

    new BukkitRunnable() {
      @Override
      public void run() {
        if (player.getLocation().distance(center) <= 1.5) {
          double damage = (highLevel ? 25 : 15) + (player.getHealth() * 0.4);
          player.damage(damage, entity);
        }
      }
    }.runTaskLater(BlightedMC.getInstance(), 15L);
  }

  private Block getSafeGround(Block base) {
    while (base.getY() > 0 && base.isPassable()) base = base.getRelative(BlockFace.DOWN);
    while (!base.isPassable()) base = base.getRelative(BlockFace.UP);
    if (base.getY() <= 0 || base.getY() >= 256) return null;
    return base.getRelative(BlockFace.DOWN);
  }

  @Override
  public void damage(double amount) {
    if (hits == 0) {
      hits = getNextHitThreshold();
    } else {
      entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1 + new Random().nextFloat() - 0.25f);
      hits--;
      return;
    }
    super.damage(amount);
  }

  private int getNextHitThreshold() {
    double current = entity.getHealth();
    if (current > maxHealth * 0.66) return 6;
    if (current > maxHealth * 0.33) return 7;
    return 8;
  }

  public void setLootTable(LootTable lootTable) {
    super.setLootTable(lootTable);
  }

  private Player getNearestPlayer(Location location, double radius) {
    double closest = Double.MAX_VALUE;
    Player nearest = null;
    for (Player player : Objects.requireNonNull(location.getWorld()).getPlayers()) {
      if (player.getGameMode() == GameMode.CREATIVE) continue;
      double dist = player.getLocation().distanceSquared(location);
      if (dist < radius * radius && dist < closest) {
        closest = dist;
        nearest = player;
      }
    }
    return nearest;
  }
}
