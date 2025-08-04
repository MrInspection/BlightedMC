package fr.moussax.blightedMC.core.registry.entities.bosses;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class AtonedHorror extends BlightedEntity {
  private final Set<TntThrow> tntThrows = new HashSet<>();
  private BukkitRunnable healingTask;
  private BukkitRunnable tntThrower;
  private BukkitRunnable rotation;
  private boolean isInThunder = false;
  private int hits = 0;

  public AtonedHorror() {
    super("Atoned Horror", 750, EntityType.ZOMBIE);
    setNameTagType(EntityNameTag.BOSS);
    addAttribute(Attribute.ATTACK_DAMAGE, 40);
  }

  @Override
  public String getEntityId() {
    return "ATONED_HORROR";
  }

  @Override
  protected void applyEquipment() {
    this.itemInMainHand = null; // No weapon
    this.armor = new ItemStack[]{
      new ItemBuilder(Material.LEATHER_BOOTS).setLeatherColor("#FFFFFF").addEnchantmentGlint().toItemStack(),
      new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherColor("#FFFFFF").addEnchantmentGlint().toItemStack(),
      new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack(),
      new ItemBuilder(Material.WHITE_STAINED_GLASS).toItemStack(),
    };
    super.applyEquipment();
  }

  @Override
  public LivingEntity spawn(Location location) {
    super.spawn(location);
    startHealing();
    restartRotation();
    startTnt();
    return entity;
  }

  private void startHealing() {
    if (healingTask != null) healingTask.cancel();

    healingTask = new BukkitRunnable() {
      @Override
      public void run() {
        if (entity == null || entity.isDead()) {
          cancel();
          return;
        }

        double healAmount = Math.min(maxHealth / 1000.0, maxHealth - entity.getHealth());
        if (healAmount > 0) {
          entity.setHealth(entity.getHealth() + healAmount);
          updateNameTag();
        }
      }
    };
    healingTask.runTaskTimer(BlightedMC.getInstance(), 20, 20);
  }

  private void restartRotation() {
    if (rotation != null) rotation.cancel();

    rotation = new BukkitRunnable() {
      @Override
      public void run() {
        startThunder();
      }
    };
    rotation.runTaskLater(BlightedMC.getInstance(), 30 * 20);
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

  private void startThunder() {
    isInThunder = true;
    final Location l = getNextFittingBlock(entity.getLocation().getBlock()).getLocation().add(0.5, 1, 0.5);

    if (tntThrower != null) tntThrower.cancel();
    tntThrows.forEach(tntThrow -> tntThrow.remove(false));
    tntThrows.clear();

    Set<Block> blocks = new HashSet<>();
    Block centerBlock = l.clone().subtract(0, 1, 0).getBlock();
    blocks.add(centerBlock);
    centerBlock.setType(Material.BEDROCK);

    Set<Block> expand = new HashSet<>();
    expand.add(centerBlock);

    tntThrower = new BukkitRunnable() {
      private int i;

      @Override
      public void run() {
        if (i > 8 * 20 + 1) return;

        entity.teleport(l);
        i++;

        if (i % 20 == 0) {
          if (i / 20 > 7) {
            for (Block block : expand) {
              block.getWorld().strikeLightningEffect(block.getLocation());

              for (Entity e : entity.getNearbyEntities(7, 7, 7)) {
                if (e instanceof Player player) {
                  hits++;
                  player.damage(4800 * hits, entity);
                }
              }
            }

            new BukkitRunnable() {
              @Override
              public void run() {
                tntThrower.cancel();
                isInThunder = false;
                startTnt();
                restartRotation();
              }
            }.runTaskLater(BlightedMC.getInstance(), 20);
          } else {
            for (Block b : new HashSet<>(expand)) {
              expand.remove(b);
              check(getNextFittingBlock(b.getLocation().add(1, 0, 0).getBlock()));
              check(getNextFittingBlock(b.getLocation().add(-1, 0, 0).getBlock()));
              check(getNextFittingBlock(b.getLocation().add(0, 0, 1).getBlock()));
              check(getNextFittingBlock(b.getLocation().add(0, 0, -1).getBlock()));
            }
          }
        }
      }

      @Override
      public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        for (Block block : blocks) {
          block.setType(Material.AIR);
        }
      }

      private void check(Block block) {
        if (block.getType() != Material.BEDROCK) {
          expand.add(block);
          blocks.add(block);
          block.setType(Material.BEDROCK);
        }
      }
    };
    tntThrower.runTaskTimer(BlightedMC.getInstance(), 0, 1);
  }

  private void startTnt() {
    if (entity.isDead() || isInThunder) return;

    tntThrower = new BukkitRunnable() {
      @Override
      public void run() {
        tntThrows.add(new TntThrow(AtonedHorror.this, entity.getHealth() <= entity.getHealth() / 3));
      }
    };
    tntThrower.runTaskLater(BlightedMC.getInstance(), 22);
  }

  @Override
  public void kill() {
    super.kill();
    if (healingTask != null) healingTask.cancel();
    if (tntThrower != null) tntThrower.cancel();
    if (rotation != null) rotation.cancel();
    tntThrows.forEach(tntThrow -> tntThrow.remove(false));
  }

  private static class TntThrow {
    private final ArmorStand stand;
    private final Map<Block, Material> materials = new HashMap<>();
    private final AtonedHorror entity;
    private final Block middle;
    private final BukkitRunnable runnable;

    public TntThrow(AtonedHorror horror, boolean isEnrage) {
      entity = horror;
      markBlocks();

      Block start = entity.getEntity().getLocation().getBlock();
      middle = getNextFittingBlock(start);

      stand = horror.getEntity().getWorld().spawn(horror.getEntity().getEyeLocation(), ArmorStand.class, s -> {
        s.setInvisible(true);
        s.setGravity(false);
        s.setInvulnerable(true);
        s.getEquipment().setHelmet(new ItemStack(Material.TNT));
      });

      double distance = stand.getLocation().distance(middle.getLocation().add(0.5, 1, 0.5));
      double speed = distance / ((isEnrage) ? 10 : 20);

      Location target = middle.getLocation().add(0.5, 1, 0.5);
      Vector dir = target.clone().subtract(stand.getEyeLocation()).toVector().normalize().multiply(speed);

      runnable = new BukkitRunnable() {
        int i = 0;
        Location l = stand.getLocation();

        @Override
        public void run() {
          if (i >= 21) {
            explode();
            remove(true);
            return;
          }

          double y = getOffset(((((isEnrage) ? 2 : 1) * i) * 0.1) * Math.PI) * 2;
          l = l.add(dir);
          stand.teleport(l.clone().add(0, y, 0));
          i++;
          if (isEnrage) i++;
        }
      };
      runnable.runTaskTimer(BlightedMC.getInstance(), 1, 1);
    }

    private double getOffset(double i) {
      return Math.sin(i * 0.5) + 0.5;
    }

    private void explode() {
      Location l = middle.getLocation().add(0.5, 1, 0.5);

      for (Entity e : middle.getWorld().getNearbyEntities(l, 3, 3, 3)) {
        if (e instanceof Player player) {
          entity.hits++;
          double damage = (l.distance(player.getLocation()) <= 2.5) ?
            player.getHealth() * 0.15 :
            player.getHealth() * 0.15 * 1.5294;
          player.damage(damage, entity.getEntity());
        }
      }
    }

    private void markBlocks() {
      Block start = entity.getEntity().getLocation().getBlock();
      start = getNextFittingBlock(start);
      materials.put(start, start.getType());
      start.setType(Material.RED_TERRACOTTA);

      makeBlock(start.getLocation().add(1, 0, 0).getBlock(), Material.RED_TERRACOTTA);
      makeBlock(start.getLocation().add(-1, 0, 0).getBlock(), Material.RED_TERRACOTTA);
      makeBlock(start.getLocation().add(0, 0, 1).getBlock(), Material.RED_TERRACOTTA);
      makeBlock(start.getLocation().add(0, 0, -1).getBlock(), Material.RED_TERRACOTTA);

      makeBlock(start.getLocation().add(2, 0, 0).getBlock(), Material.WHITE_TERRACOTTA);
      makeBlock(start.getLocation().add(-2, 0, 0).getBlock(), Material.WHITE_TERRACOTTA);
      makeBlock(start.getLocation().add(0, 0, 2).getBlock(), Material.WHITE_TERRACOTTA);
      makeBlock(start.getLocation().add(0, 0, -2).getBlock(), Material.WHITE_TERRACOTTA);

      makeBlock(start.getLocation().add(1, 0, 1).getBlock(), Material.WHITE_TERRACOTTA);
      makeBlock(start.getLocation().add(-1, 0, 1).getBlock(), Material.WHITE_TERRACOTTA);
      makeBlock(start.getLocation().add(1, 0, -1).getBlock(), Material.WHITE_TERRACOTTA);
      makeBlock(start.getLocation().add(-1, 0, -1).getBlock(), Material.WHITE_TERRACOTTA);

      makeBlock(start.getLocation().add(2, 0, 1).getBlock(), Material.LIGHT_GRAY_TERRACOTTA);
      makeBlock(start.getLocation().add(-2, 0, 1).getBlock(), Material.LIGHT_GRAY_TERRACOTTA);
      makeBlock(start.getLocation().add(2, 0, -1).getBlock(), Material.LIGHT_GRAY_TERRACOTTA);
      makeBlock(start.getLocation().add(-2, 0, -1).getBlock(), Material.LIGHT_GRAY_TERRACOTTA);

      makeBlock(start.getLocation().add(1, 0, 2).getBlock(), Material.LIGHT_GRAY_TERRACOTTA);
      makeBlock(start.getLocation().add(-1, 0, -2).getBlock(), Material.LIGHT_GRAY_TERRACOTTA);
      makeBlock(start.getLocation().add(1, 0, -2).getBlock(), Material.LIGHT_GRAY_TERRACOTTA);
      makeBlock(start.getLocation().add(-1, 0, 2).getBlock(), Material.LIGHT_GRAY_TERRACOTTA);
    }

    private void makeBlock(Block b, Material m) {
      b = getNextFittingBlock(b);
      materials.put(b, b.getType());
      b.setType(m);
    }

    public void remove(boolean next) {
      for (Map.Entry<Block, Material> entry : materials.entrySet()) {
        entry.getKey().setType(entry.getValue());
      }
      stand.remove();

      try {
        runnable.cancel();
      } catch (Exception ignored) {
      }

      if (next) {
        new BukkitRunnable() {
          @Override
          public void run() {
            entity.startTnt();
          }
        }.runTaskLater(BlightedMC.getInstance(), 1);
      }
    }
  }
}
