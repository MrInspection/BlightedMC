package fr.moussax.blightedMC.core.entities.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.Formatter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BlightedEntitiesListener implements Listener {

  private static final Map<UUID, BlightedEntity> blightedEntities = new HashMap<>();
  private final Map<Entity, Integer> damageIndicators = new HashMap<>();

  public static void registerEntity(LivingEntity entity, BlightedEntity blighted) {
    blightedEntities.put(entity.getUniqueId(), blighted);
  }

  private double getRandomOffset() {
    double random = Math.random();
    if (Math.random() > 0.5) random *= -1;
    return random;
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof LivingEntity entity)) return;

    BlightedEntity blighted = blightedEntities.get(entity.getUniqueId());
    if (blighted == null) return;

    double damage = event.getFinalDamage();
    double remainingHealth = entity.getHealth() + entity.getAbsorptionAmount() - damage;

    if (remainingHealth > 0) {
      blighted.updateNameTag();
    }

    Location location = entity.getLocation().clone().add(getRandomOffset(), 1.0, getRandomOffset());
    World world = location.getWorld();

    assert world != null;
    world.spawn(location, ArmorStand.class, stand -> {
      stand.setMarker(true);
      stand.setVisible(false);
      stand.setGravity(false);
      stand.setSmall(true);
      stand.setCustomNameVisible(true);
      stand.setCustomName("§c" + Formatter.formatDouble(damage, 2));
      damageIndicators.put(stand, 30);
    });

    new BukkitRunnable() {
      final List<Entity> toRemove = new ArrayList<>();

      @Override
      public void run() {
        Iterator<Map.Entry<Entity, Integer>> iterator = damageIndicators.entrySet().iterator();

        while (iterator.hasNext()) {
          Map.Entry<Entity, Integer> entry = iterator.next();
          Entity stand = entry.getKey();
          int ticks = entry.getValue() - 1;

          if (ticks <= 0 || stand.isDead()) {
            stand.remove();
            iterator.remove();
          } else {
            entry.setValue(ticks);
          }
        }

        if (damageIndicators.isEmpty()) cancel();
      }
    }.runTaskTimer(BlightedMC.getInstance(), 0L, 1L);

    new BukkitRunnable() {
      @Override
      public void run() {
        if (entity.isDead()) return;
        blighted.updateNameTag();
      }
    }.runTaskLater(BlightedMC.getInstance(), 1L); // 1 tick delay
  }

  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    BlightedEntity blighted = blightedEntities.remove(event.getEntity().getUniqueId());
    if (blighted == null) return;

    Player killer = event.getEntity().getKiller();
    BlightedPlayer player = (killer != null) ? BlightedPlayer.getBlightedPlayer(killer) : null;

    blighted.dropLoot(event.getEntity().getLocation(), player);

    event.getDrops().clear();
    event.setDroppedExp(blighted.getDroppedExp());
  }
}
