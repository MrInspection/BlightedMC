package fr.moussax.blightedMC.core.entities.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntityAttachment;
import fr.moussax.blightedMC.core.entities.EntitiesRegistry;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.Formatter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class BlightedEntitiesListener implements Listener {
  private static final Map<UUID, BlightedEntity> blightedEntities = new HashMap<>();
  private final Map<Entity, Integer> damageIndicators = new HashMap<>();

  // Prevent infinite recursion when forwarding damage
  private final ThreadLocal<Set<UUID>> processingDamage = ThreadLocal.withInitial(HashSet::new);

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

    if (processingDamage.get().contains(entity.getUniqueId())) return;
    processingDamage.get().add(entity.getUniqueId());

    try {
      BlightedEntity blighted = blightedEntities.get(entity.getUniqueId());
      EntityAttachment attachment = BlightedEntity.getAttachment(entity);

      if (attachment != null) {
        // Forward damage from attachment to owner
        LivingEntity ownerEntity = attachment.owner().getEntity();
        if (ownerEntity != null && !ownerEntity.isDead()) {
          ownerEntity.damage(event.getFinalDamage(), getRealDamager(event));
          syncArmor((LivingEntity) attachment.entity(), ownerEntity);
        }
        event.setCancelled(true);
        return;
      }

      if (blighted != null) {
        // Forward damage from owner to attachments
        for (EntityAttachment att : blighted.attachments) {
          if (att.entity() instanceof LivingEntity living && !living.isDead()) {
            living.damage(event.getFinalDamage(), getRealDamager(event));
            syncArmor(living, entity);
          }
        }

        if (event instanceof EntityDamageByEntityEvent e) {
          Player player = getPlayerDamager(e.getDamager());
          if (blighted.isImmuneTo(entity, event)) {
            event.setCancelled(true);
            if (player != null) {
              player.sendMessage("§4 ■ §cThis creature is immune to this type of damage!");
              player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 0.6f);
            }
            return;
          }
        }

        double damage = event.getFinalDamage();
        double remainingHealth = entity.getHealth() + entity.getAbsorptionAmount() - damage;

        if (remainingHealth > 0) {
          blighted.updateNameTag();
        } else {
          blighted.killAllAttachments();
        }

        spawnDamageIndicator(entity, damage);

        new BukkitRunnable() {
          @Override
          public void run() {
            if (entity.isDead()) return;
            blighted.updateNameTag();
          }
        }.runTaskLater(BlightedMC.getInstance(), 1L);
      }
    } finally {
      processingDamage.get().remove(entity.getUniqueId());
    }
  }

  @EventHandler
  public void onEntityHeal(EntityRegainHealthEvent event) {
    if (!(event.getEntity() instanceof LivingEntity entity)) return;

    BlightedEntity blighted = blightedEntities.get(entity.getUniqueId());
    EntityAttachment attachment = BlightedEntity.getAttachment(entity);

    if (blighted != null) {
      blighted.updateNameTag();
      for (EntityAttachment att : blighted.attachments) {
        if (att.entity() instanceof LivingEntity living && !living.isDead()) {
          living.setHealth(Math.min(living.getHealth(), entity.getHealth()));
          syncArmor(living, entity);
        }
      }
    } else if (attachment != null) {
      LivingEntity ownerEntity = attachment.owner().getEntity();
      if (ownerEntity != null && !ownerEntity.isDead()) {
        ownerEntity.setHealth(Math.min(ownerEntity.getHealth(), entity.getHealth()));
        syncArmor((LivingEntity) attachment.entity(), ownerEntity);
      }
    }
  }

  @EventHandler
  public void onEntityDeath(EntityDeathEvent event) {
    BlightedEntity blighted = blightedEntities.remove(event.getEntity().getUniqueId());
    if (blighted != null) {
      blighted.removeBossBar();
      Player killer = event.getEntity().getKiller();
      BlightedPlayer player = (killer != null) ? BlightedPlayer.getBlightedPlayer(killer) : null;
      blighted.dropLoot(event.getEntity().getLocation(), player);
      event.getDrops().clear();
      event.setDroppedExp(blighted.getDroppedExp());
    } else {
      EntityAttachment attachment = BlightedEntity.getAttachment(event.getEntity());
      if (attachment != null) {
        LivingEntity ownerEntity = attachment.owner().getEntity();
        if (ownerEntity != null && !ownerEntity.isDead()) {
          ownerEntity.setHealth(0);
        }
      }
    }
  }

  @EventHandler
  public void onChunkLoad(ChunkLoadEvent event) {
    rehydrateChunk(event.getChunk());
  }

  public static void rehydrateChunk(Chunk chunk) {
    NamespacedKey key = new NamespacedKey(BlightedMC.getInstance(), "entityId");
    for (Entity e : chunk.getEntities()) {
      if (!(e instanceof LivingEntity living)) continue;
      PersistentDataContainer pdc = living.getPersistentDataContainer();
      String id = pdc.get(key, PersistentDataType.STRING);
      if (id == null || id.isEmpty()) continue;

      if (blightedEntities.containsKey(living.getUniqueId())) continue;

      BlightedEntity proto = EntitiesRegistry.getEntity(id);
      if (proto == null) continue;

      BlightedEntity instance;
      try {
        instance = proto.getClass().getDeclaredConstructor().newInstance();
      } catch (Exception ex) {
        instance = proto;
      }
      instance.attachToExisting(living);

      // Sync attachments armor and health on rehydrate
      for (EntityAttachment att : instance.attachments) {
        if (att.entity() instanceof LivingEntity livingAtt && !livingAtt.isDead()) {
          livingAtt.setHealth(instance.getMaxHealth());
          livingAtt.setHealth(instance.getMaxHealth());
          syncArmor(livingAtt, living);
        }
      }
    }
  }

  private void spawnDamageIndicator(LivingEntity entity, double damage) {
    Location location = entity.getLocation().clone().add(getRandomOffset(), 1.0, getRandomOffset());
    World world = location.getWorld();
    if (world == null) return;

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
  }

  private Player getPlayerDamager(Entity damager) {
    if (damager instanceof Player p) return p;
    if (damager instanceof Projectile projectile) {
      ProjectileSource shooter = projectile.getShooter();
      if (shooter instanceof Player player) return player;
    }
    return null;
  }

  private Entity getRealDamager(EntityDamageEvent event) {
    if (event instanceof EntityDamageByEntityEvent e) {
      return e.getDamager();
    }
    return null;
  }

  // Sync armor from source to target (owner <- attachment or vice versa)
  private static void syncArmor(LivingEntity target, LivingEntity source) {
    EntityEquipment sourceEquip = source.getEquipment();
    EntityEquipment targetEquip = target.getEquipment();
    if (sourceEquip == null || targetEquip == null) return;

    targetEquip.setHelmet(sourceEquip.getHelmet());
    targetEquip.setChestplate(sourceEquip.getChestplate());
    targetEquip.setLeggings(sourceEquip.getLeggings());
    targetEquip.setBoots(sourceEquip.getBoots());
    targetEquip.setItemInMainHand(sourceEquip.getItemInMainHand());
    targetEquip.setItemInOffHand(sourceEquip.getItemInOffHand());
  }
}
