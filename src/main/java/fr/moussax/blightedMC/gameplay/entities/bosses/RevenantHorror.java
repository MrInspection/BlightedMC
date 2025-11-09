package fr.moussax.blightedMC.gameplay.entities.bosses;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class RevenantHorror extends BlightedEntity {
  private boolean inEnrageState = false;
  private BukkitRunnable abilityRunnable;

  public RevenantHorror() {
    super("Revenant Horror", 250, 30, EntityType.ZOMBIE);
    this.entityId = "REVENANT_HORROR";
    setNameTagType(EntityNameTag.BOSS);

    addRepeatingTask(() -> {
      abilityRunnable = new BukkitRunnable() {
        private int phase = 0;

        @Override
        public void run() {
          if (entity == null || entity.isDead()) {
            stopAbility();
            cancel();
            return;
          }

          switch (phase) {
            case 0 -> performLifeDrain();
            case 1 -> performPestilence();
            case 2 -> startEnragePhase();
          }

          phase = (phase + 1) % 3;
        }
      };
      return abilityRunnable;
    }, 20L * 10, 20L * 10);
  }

  @Override
  protected void applyEquipment() {
    this.itemInMainHand = new ItemBuilder(Material.DIAMOND_HOE).addEnchantmentGlint().toItemStack();
    this.armor = new ItemStack[]{
      new ItemBuilder(Material.DIAMOND_BOOTS).addEnchantmentGlint().toItemStack(),
      new ItemBuilder(Material.CHAINMAIL_LEGGINGS).addEnchantmentGlint().toItemStack(),
      new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack(),
      new ItemBuilder(Material.PLAYER_HEAD).setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhiZWUyM2I1YzcyNmFlOGUzZDAyMWU4YjRmNzUyNTYxOWFiMTAyYTRlMDRiZTk4M2I2MTQxNDM0OWFhYWM2NyJ9fX0=").toItemStack(),
    };
    super.applyEquipment();
  }

  private void performLifeDrain() {
    double newHealth = Math.min(
      entity.getHealth() + getDamage(),
      Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getBaseValue()
    );
    entity.setHealth(newHealth);
    updateNameTag();

    entity.getWorld().spawnParticle(
      Particle.HEART,
      entity.getLocation().add(0, entity.getEyeHeight() / 2, 0),
      8, 0.5, 0.5, 0.5
    );
    entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1f, 0.7f);
  }

  private void performPestilence() {
    List<Player> nearbyPlayers = entity.getWorld().getPlayers();
    for (Player player : nearbyPlayers) {
      if (player.getLocation().distanceSquared(entity.getLocation()) <= 6 * 6) {
        player.damage(2.0, entity);
        player.spawnParticle(Particle.SNEEZE, player.getLocation().add(0, 1, 0), 8, 0.3, 0.5, 0.3, 0.01);
        player.playSound(player.getLocation(), Sound.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, 1f, 0.8f);
      }
    }
  }

  private void startEnragePhase() {
    if (inEnrageState) return;

    inEnrageState = true;
    equipEnrage();

    entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.2f, 0.6f);
    entity.getWorld().spawnParticle(Particle.LARGE_SMOKE, entity.getLocation().add(0, 1, 0), 20, 0.6, 0.8, 0.6);

    new BukkitRunnable() {
      @Override
      public void run() {
        inEnrageState = false;
        if (entity instanceof Zombie zombie) equipNormal(zombie);
      }
    }.runTaskLater(BlightedMC.getInstance(), 20 * 8); // 8s enrage duration
  }

  private void equipEnrage() {
    if (entity instanceof Zombie zombie) {
      Objects.requireNonNull(zombie.getEquipment()).setChestplate(
        new ItemBuilder(Material.LEATHER_CHESTPLATE)
          .setLeatherColor("#FF4B4B")
          .toItemStack()
      );
    }
  }

  private void equipNormal(Zombie zombie) {
    Objects.requireNonNull(zombie.getEquipment()).setChestplate(
      new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack()
    );
  }

  public int getDamage() {
    return inEnrageState ? damage * 3 : damage;
  }

  private void stopAbility() {
    if (abilityRunnable != null) {
      try {
        abilityRunnable.cancel();
      } catch (IllegalStateException ignored) {}
      abilityRunnable = null;
    }
  }

  @Override
  public void kill() {
    stopAbility();
    super.kill();
  }

  @Override
  public RevenantHorror clone() {
    RevenantHorror clone = (RevenantHorror) super.clone();
    clone.inEnrageState = false;
    clone.abilityRunnable = null;

    clone.addRepeatingTask(() -> {
      clone.abilityRunnable = new BukkitRunnable() {
        private int phase = 0;

        @Override
        public void run() {
          if (clone.entity == null || clone.entity.isDead()) {
            clone.stopAbility();
            cancel();
            return;
          }

          switch (phase) {
            case 0 -> clone.performLifeDrain();
            case 1 -> clone.performPestilence();
            case 2 -> clone.startEnragePhase();
          }

          phase = (phase + 1) % 3;
        }
      };
      return clone.abilityRunnable;
    }, 20L * 10, 20L * 10);

    return clone;
  }
}
