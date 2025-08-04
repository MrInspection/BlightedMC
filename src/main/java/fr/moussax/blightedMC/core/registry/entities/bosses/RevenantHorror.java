package fr.moussax.blightedMC.core.registry.entities.bosses;

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

public class RevenantHorror extends BlightedEntity {
  private BukkitRunnable abilityCycleTask;
  private boolean inEnrageState = false;
  private final int baseDamage = 30;

  public RevenantHorror() {
    super("Revenant Horror", 250, EntityType.ZOMBIE);
    setNameTagType(EntityNameTag.BOSS);
    addAttribute(Attribute.ATTACK_DAMAGE, baseDamage);
  }

  @Override
  public String getEntityId() {
    return "REVENANT_HORROR";
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

  @Override
  public LivingEntity spawn(Location location) {
    super.spawn(location);

    // Start the ability cycle
    startAbilityCycle();

    return entity;
  }

  private void startAbilityCycle() {
    if (abilityCycleTask != null) abilityCycleTask.cancel();

    abilityCycleTask = new BukkitRunnable() {
      private int phase = 0; // 0=LifeDrain, 1=Pestilence, 2=Enrage

      @Override
      public void run() {
        if (entity == null || entity.isDead()) {
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

    // 10s per phase
    abilityCycleTask.runTaskTimer(BlightedMC.getInstance(), 20 * 10, 20 * 10);
  }

    /* -----------------------
       ABILITIES
    ------------------------ */

  private void performLifeDrain() {
    double newHealth = Math.min(
      entity.getHealth() + getDamage(),
      entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue()
    );
    entity.setHealth(newHealth);
    updateNameTag(); // Update nametag and bossbar after healing

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
      zombie.getEquipment().setChestplate(
        new ItemBuilder(Material.LEATHER_CHESTPLATE)
          .setLeatherColor("#FF4B4B")
          .toItemStack()
      );
    }
  }

  private void equipNormal(Zombie zombie) {
    zombie.getEquipment().setChestplate(
      new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchantmentGlint().toItemStack()
    );
  }

  public int getDamage() {
    return inEnrageState ? baseDamage * 3 : baseDamage;
  }

  @Override
  public void kill() {
    super.kill();
    if (abilityCycleTask != null) {
      abilityCycleTask.cancel();
      abilityCycleTask = null;
    }
  }
}
