package fr.moussax.blightedMC.core.registry.entities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityAttributes;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.core.entities.LootTable.LootTable;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

@EntityAttributes({EntityAttributes.Attributes.PROJECTILE_IMMUNITY})
public class GaiaConstruct extends BlightedEntity {
  private int hits;

  public GaiaConstruct() {
    super("§6Gaia Construct", 200, EntityType.IRON_GOLEM);
    this.hits = 6;
    setNameTagType(EntityNameTag.SMALL_NUMBER);
    addAttribute(Attribute.ATTACK_DAMAGE, 15);
  }

  @Override
  public String getEntityId() {
    return "GAIA_CONSTRUCT";
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
    Block centerBlock = getNextFittingBlock(player.getLocation().getBlock());
    if (centerBlock == null) return;

    Map<Block, Material> materials = new HashMap<>();
    Set<Block> positions = new HashSet<>();
    
    // Add a center block
    positions.add(centerBlock);
    materials.put(centerBlock, centerBlock.getType());
    centerBlock.setType(Material.IRON_BLOCK);
    
    // Add surrounding blocks
    addBlock(centerBlock.getRelative(BlockFace.NORTH), positions, materials);
    addBlock(centerBlock.getRelative(BlockFace.SOUTH), positions, materials);
    addBlock(centerBlock.getRelative(BlockFace.EAST), positions, materials);
    addBlock(centerBlock.getRelative(BlockFace.WEST), positions, materials);

    Location center = centerBlock.getLocation().add(0.5, 1, 0.5);
    Objects.requireNonNull(center.getWorld()).playSound(center, Sound.BLOCK_ANVIL_PLACE, 1, 1.5f);
    center.getWorld().strikeLightningEffect(center);

    // Restore blocks after 15 ticks
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Map.Entry<Block, Material> entry : materials.entrySet()) {
          entry.getKey().setType(entry.getValue());
        }
      }
    }.runTaskLater(BlightedMC.getInstance(), 15L);

    new BukkitRunnable() {
      @Override
      public void run() {
        if (player.getLocation().distance(center) <= 1.5) {
          double damage = 15 + (player.getHealth() * 0.4);
          player.damage(damage, entity);
        }
      }
    }.runTaskLater(BlightedMC.getInstance(), 15L);
  }

  private void addBlock(Block block, Set<Block> positions, Map<Block, Material> materials) {
    Block fittingBlock = getNextFittingBlock(block);
    if (fittingBlock != null) {
      positions.add(fittingBlock);
      materials.put(fittingBlock, fittingBlock.getType());
      fittingBlock.setType(Material.IRON_BLOCK);
    }
  }

  private static Block getNextFittingBlock(Block b) {
    if (b.isPassable()) {
      while (b.isPassable()) {
        b = b.getLocation().subtract(0, 1, 0).getBlock();
      }
      return b;
    } else {
      while (!b.isPassable()) {
        b = b.getLocation().add(0, 1, 0).getBlock();
      }
      return b.getLocation().subtract(0, 1, 0).getBlock();
    }
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
