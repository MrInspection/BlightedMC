package fr.moussax.blightedMC.core.registry.entities.spawnable.blighted;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.core.entities.LootTable.LootTable;
import fr.moussax.blightedMC.core.entities.LootTable.LootDropRarity;
import fr.moussax.blightedMC.core.entities.spawning.SpawnableEntity;
import fr.moussax.blightedMC.core.entities.spawning.condition.BiomeCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.ChanceCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.TimeCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.WeatherCondition;
import fr.moussax.blightedMC.core.entities.spawning.condition.YLevelCondition;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Set;

public class BlightedZombie extends SpawnableEntity {
  private BukkitRunnable speedAbilityTask;
  private boolean hasSpeedBoost = false;

  public BlightedZombie() {
    super("BLIGHTED_ZOMBIE", "Zombie", 30, EntityType.ZOMBIE, 0.15);
    setNameTagType(EntityNameTag.BLIGHTED);
    setDamage(10);
    setDroppedExp(10);
    setLootTable(createLootTable());

    armor = new ItemStack[]{
      new ItemStack(Material.AIR),
      new ItemStack(Material.AIR),
      new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor("#81CFE2").toItemStack(),
      new ItemBuilder(Material.LEATHER_HELMET).setLeatherColor("#81CFE2").toItemStack()
    };

    // Register ability factory with lifecycle manager
    addRepeatingTask(() -> {
      BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
          if (entity == null || entity.isDead()) {
            stopSpeedAbility();
            cancel();
            return;
          }
          startSpeedAbility();
        }
      };
      speedAbilityTask = task;
      return task;
    }, 100L, 200L);
  }

  @Override
  public SpawnableEntity clone() {
    BlightedZombie clone = (BlightedZombie) super.clone();
    clone.speedAbilityTask = null;
    clone.hasSpeedBoost = false;

    clone.addRepeatingTask(() -> {
      BukkitRunnable task = new BukkitRunnable() {
        @Override
        public void run() {
          if (clone.entity == null || clone.entity.isDead()) {
            clone.stopSpeedAbility();
            cancel();
            return;
          }
          clone.startSpeedAbility();
        }
      };
      clone.speedAbilityTask = task;
      return task;
    }, 100L, 200L);

    return clone;
  }

  @Override
  public LivingEntity spawn(Location location) {
    super.spawn(location);
    return entity;
  }

  private void startSpeedAbility() {
    if (hasSpeedBoost || entity == null) return;

    hasSpeedBoost = true;
    entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 90f, 1f);
    equipSpeedArmor();
    entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 2));

    new BukkitRunnable() {
      @Override
      public void run() {
        hasSpeedBoost = false;
        equipNormalArmor();
      }
    }.runTaskLater(BlightedMC.getInstance(), 20 * 10);
  }

  private void stopSpeedAbility() {
    if (speedAbilityTask != null) {
      try {
        speedAbilityTask.cancel();
      } catch (IllegalStateException ignored) {
      }
      speedAbilityTask = null;
    }
    hasSpeedBoost = false;
    if (entity != null && !entity.isDead()) {
      equipNormalArmor();
    }
  }

  @Override
  public void kill() {
    stopSpeedAbility();
    super.kill();
  }

  private void equipSpeedArmor() {
    if (!(entity instanceof Zombie zombie)) return;

    Objects.requireNonNull(zombie.getEquipment()).setChestplate(
      new ItemBuilder(Material.LEATHER_CHESTPLATE)
        .setLeatherColor("#81CFE2")
        .setArmorTrim(TrimMaterial.DIAMOND, TrimPattern.FLOW)
        .toItemStack()
    );
    zombie.getEquipment().setHelmet(
      new ItemBuilder(Material.LEATHER_HELMET)
        .setLeatherColor("#81CFE2")
        .setArmorTrim(TrimMaterial.DIAMOND, TrimPattern.FLOW)
        .toItemStack()
    );
  }

  private void equipNormalArmor() {
    if (!(entity instanceof Zombie zombie)) return;

    Objects.requireNonNull(zombie.getEquipment()).setChestplate(
      new ItemBuilder(Material.LEATHER_CHESTPLATE)
        .setLeatherColor("#81CFE2")
        .toItemStack()
    );
    zombie.getEquipment().setHelmet(
      new ItemBuilder(Material.LEATHER_HELMET)
        .setLeatherColor("#81CFE2")
        .toItemStack()
    );
  }

  private LootTable createLootTable() {
    return new LootTable()
      .setMaxDrop(2)
      .addLoot(Material.ROTTEN_FLESH, 1, 3, 1, LootDropRarity.COMMON)
      .addFavorsLoot(5, 0.03, LootDropRarity.EXTRAORDINARY);
  }

  @Override
  protected void setupSpawnConditions() {
    addSpawnCondition(new BiomeCondition(Set.of(
      Biome.FOREST,
      Biome.BIRCH_FOREST,
      Biome.DARK_FOREST,
      Biome.FLOWER_FOREST
    )));
    addSpawnCondition(new TimeCondition(true));
    addSpawnCondition(new WeatherCondition(WeatherCondition.WeatherType.CLEAR));
    addSpawnCondition(new YLevelCondition(50, 80));
    addSpawnCondition(new ChanceCondition(0.5));
  }
}
