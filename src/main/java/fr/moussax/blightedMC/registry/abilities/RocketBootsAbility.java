package fr.moussax.blightedMC.registry.abilities;

import fr.moussax.blightedMC.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.index.qual.Positive;

import java.util.concurrent.ThreadLocalRandom;

public class RocketBootsAbility implements FullSetBonus, Listener {
  private BlightedPlayer player;

  @Override
  public void startAbilityEffect() {
    if (player == null) return;
    Player p = player.getPlayer();
    if (p == null) return;
    p.setAllowFlight(true);
  }

  @Override
  public void stopAbilityEffect() {
    if (player == null) return;
    Player p = player.getPlayer();
    if (p == null) return;
    p.setAllowFlight(false);
  }

  @Override
  public int getPieces() { return 1; }

  @Override
  public int getMaxPieces() { return 1; }

  @Override
  public void setPlayer(BlightedPlayer player) { this.player = player; }

  @Override
  public BlightedPlayer getAbilityOwner() { return this.player; }

  @EventHandler
  public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
    var p = event.getPlayer();
    
    if (isAbilityOwner(p)) {
      return;
    }
    
    if(p.getGameMode() == GameMode.CREATIVE) return;

    if(!p.isFlying()) {
      event.setCancelled(true);
      p.setAllowFlight(false);
      p.setVelocity(p.getLocation().getDirection().setY(0.5).multiply(1.25));
      applyDurabilityDamageToBoots(p, 1);
      p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 12, 0.2, 0.05, 0.2, 0.001);
      p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 60f, 0f);
    }
  }

  @EventHandler
  public void onPlayerLand(PlayerMoveEvent event) {
    Player p = event.getPlayer();
    
    if (isAbilityOwner(p)) {
      return;
    }
    
    if(((Entity) p).isOnGround()) {
      p.setAllowFlight(true);
    }
  }

  private void applyDurabilityDamageToBoots(Player player, @Positive int damage) {
    ItemStack boots = player.getInventory().getBoots();
    if (boots == null) return;
    if (!boots.hasItemMeta()) return;

    ItemMeta meta = boots.getItemMeta();
    if (!(meta instanceof Damageable damageable)) return;

    int unbreakingLevel = boots.getEnchantmentLevel(Enchantment.UNBREAKING);

    int appliedDamage = 0;
    for (int i = 0; i < damage; i++) {
      if (unbreakingLevel > 0) {
        double skipChance = (double) unbreakingLevel / (unbreakingLevel + 1);
        if (ThreadLocalRandom.current().nextDouble() < skipChance) {
          continue; // durability loss skipped
        }
      }
      appliedDamage++;
    }

    if (appliedDamage <= 0) return;

    int currentDamage = damageable.getDamage();
    int maxDurability = boots.getType().getMaxDurability();

    int durabilityDamage = currentDamage + appliedDamage;
    if (durabilityDamage >= maxDurability) {
      player.getInventory().setBoots(null);
      player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
    } else {
      damageable.setDamage(durabilityDamage);
      boots.setItemMeta(meta);
    }
  }
}
