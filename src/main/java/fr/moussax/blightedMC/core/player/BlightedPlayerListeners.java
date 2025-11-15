package fr.moussax.blightedMC.core.player;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;

public class BlightedPlayerListeners implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    new BlightedPlayer(event.getPlayer());
    event.setJoinMessage("§8 ■ §f" + event.getPlayer().getName() + " §7joined the SMP.");
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    BlightedPlayer player = BlightedPlayer.getBlightedPlayer(event.getPlayer());
    if (player != null) {
      player.saveData();
      BlightedPlayer.removePlayer(event.getPlayer());
    }
    event.setQuitMessage("§8 ■ §f" + event.getPlayer().getName() + " §7left the SMP.");
  }

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();
    String message = event.getMessage();
    event.setFormat(" §7" + player.getName() + "§8 » §f" + message);
  }

  @EventHandler
  public void onPlayerDamage(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player damaged)) return;

    if (event.getDamager() instanceof Arrow arrow) {
      if (!(arrow.getShooter() instanceof Player damager)) return;
      if (damaged == damager) return;

      double targetHealth = damaged.getHealth() - event.getFinalDamage();
      if (targetHealth < 0) targetHealth = 0;

      double maxHealth = Objects.requireNonNull(damaged.getAttribute(Attribute.MAX_HEALTH)).getValue();
      double percentage = (targetHealth > 0) ? (targetHealth / maxHealth) * 100.0 : 0.0;
      String colorPrefix = percentage <= 20 ? "§c" : (percentage <= 50 ? "§e" : "§a");

      damager.sendMessage(
        "§c \uD83C\uDFF9 §f" + damaged.getName() +
          " §7is now at " +
          colorPrefix + (int) targetHealth + "❤" + "§7."
      );
    }
  }

  private final Map<UUID, Entity> lastDamagerMap = new HashMap<>();

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player victim)) return;

    Entity damager = event.getDamager();
    if (damager instanceof Projectile projectile) {
      ProjectileSource shooter = projectile.getShooter();
      if (shooter instanceof Entity shooterEntity) {
        lastDamagerMap.put(victim.getUniqueId(), shooterEntity);
        return;
      }
    }
    lastDamagerMap.put(victim.getUniqueId(), damager);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player victim = event.getEntity();
    String vanillaDeathMessage = event.getDeathMessage();
    if (vanillaDeathMessage == null) return;

    Entity killer = lastDamagerMap.get(victim.getUniqueId());
    lastDamagerMap.remove(victim.getUniqueId());

    if (killer != null && killer.equals(victim)) {
      event.setDeathMessage("§c ☠ §f" + victim.getName() + " §7took their own life.");
      return;
    }

    String distanceSuffix = "";
    if (killer != null) {
      double dist = killer.getLocation().distance(victim.getLocation());
      if (dist > 35) {
        distanceSuffix = " from §e" + (int) dist + "§7 blocks away";
      }
      if (dist >= 55) {
        distanceSuffix = " from §c§l" + (int) dist + "§7 blocks away. It's probably the gaming chair";
        victim.getWorld().playSound(victim.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 0f);
      }
    }

    String killerName = null;
    if (killer != null) {
      BlightedEntity blighted = BlightedEntitiesListener.getBlightedEntity(killer);
      if (blighted != null) {
        killerName = blighted.getName();
      } else {
        killerName = killer.getName();
      }
    }

    String cleanedMessage = vanillaDeathMessage.replaceFirst(" using .*$", "");
    String rest = cleanedMessage.replaceFirst(java.util.regex.Pattern.quote(victim.getName()), "");
    if (killerName != null) {
      event.setDeathMessage("§c ☠ §f" + victim.getName() + "§7 was slain by " + killerName + distanceSuffix + "§7.");
    } else {
      event.setDeathMessage("§c ☠ §f" + victim.getName() + "§7" + rest + distanceSuffix + "§7.");
    }
  }
}
