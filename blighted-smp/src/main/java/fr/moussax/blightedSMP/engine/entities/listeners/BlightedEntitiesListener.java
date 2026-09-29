package fr.moussax.blightedSMP.engine.entities.listeners;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.entities.BlightedEntity;
import fr.moussax.blightedSMP.engine.entities.EntityManager;
import fr.moussax.blightedSMP.engine.entities.attachment.AttachmentRole;
import fr.moussax.blightedSMP.engine.entities.attachment.EntityAttachment;
import fr.moussax.blightedSMP.engine.entities.defense.EntityImmunity;
import fr.moussax.blightedSMP.engine.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static fr.moussax.blightedSMP.engine.entities.BlightedEntity.*;

/**
 * Event listener handling damage, potion effects, health updates, death, and chunk loading
 * for {@link BlightedEntity} instances and delegating runtime entity tracking to {@link EntityManager}.
 */
public final class BlightedEntitiesListener implements Listener {

    private final Set<UUID> processingDamageIds = ConcurrentHashMap.newKeySet();

    public BlightedEntitiesListener() {
        EntityManager.initialize();
    }

    /**
     * Delegates entity registration to {@link EntityManager#registerEntity(LivingEntity, BlightedEntity)}.
     *
     * @param entity   living entity to track
     * @param blighted blighted entity wrapper instance
     */
    public static void registerEntity(LivingEntity entity, BlightedEntity blighted) {
        EntityManager.registerEntity(entity, blighted);
    }

    /**
     * Delegates entity unregistration to {@link EntityManager#unregisterEntity(LivingEntity)}.
     *
     * @param entity living entity to stop tracking
     */
    public static void unregisterEntity(LivingEntity entity) {
        EntityManager.unregisterEntity(entity);
    }

    /**
     * Delegates attachment registration to {@link EntityManager#registerAttachment(Entity, BlightedEntity)}.
     *
     * @param attachment attachment entity
     * @param owner      owning blighted entity wrapper
     */
    public static void registerAttachment(Entity attachment, BlightedEntity owner) {
        EntityManager.registerAttachment(attachment, owner);
    }

    /**
     * Delegates attachment unregistration to {@link EntityManager#unregisterAttachment(Entity)}.
     *
     * @param attachment attachment entity to unregister
     */
    public static void unregisterAttachment(Entity attachment) {
        EntityManager.unregisterAttachment(attachment);
    }

    /**
     * Delegates entity lookup to {@link EntityManager#getBlightedEntity(Entity)}.
     *
     * @param entity target entity
     * @return blighted entity wrapper, or {@code null} if untracked
     */
    public static BlightedEntity getBlightedEntity(Entity entity) {
        return EntityManager.getBlightedEntity(entity);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent damageByEntity) {
            handleDamageDealt(damageByEntity);
        }

        Entity target = event.getEntity();
        if (!target.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        UUID entityId = target.getUniqueId();
        if (!processingDamageIds.add(entityId)) return;

        try {
            BlightedEntity owner = EntityManager.getAttachmentOwner(entityId);
            if (owner != null) {
                LivingEntity ownerEntity = owner.getEntity();
                if (ownerEntity == null || ownerEntity.isDead()) {
                    target.remove();
                    EntityManager.removeAttachmentOwner(entityId);
                    return;
                }

                AttachmentRole role = getAttachmentRole(owner, target);
                if (role == AttachmentRole.HITBOX) {
                    handleAttachmentDamage(owner, target, event);
                    return;
                }
            }

            if (!(target instanceof LivingEntity living)) {
                return;
            }
            BlightedEntity blighted = EntityManager.getDirectBlightedEntity(entityId);
            if (blighted == null) {
                return;
            }
            handleBlightedEntityDamage(blighted, living, event);
        } finally {
            processingDamageIds.remove(entityId);
        }
    }

    private AttachmentRole getAttachmentRole(BlightedEntity owner, Entity attachmentEntity) {
        if (attachmentEntity == null) {
            return AttachmentRole.SUBORDINATE;
        }

        if (owner != null && owner.attachments != null) {
            for (EntityAttachment attachment : owner.attachments) {
                if (attachmentEntity.equals(attachment.entity())) {
                    return attachment.role();
                }
            }
        }

        PersistentDataContainer persistentDataContainer = attachmentEntity.getPersistentDataContainer();
        String roleString = persistentDataContainer.get(ATTACHMENT_ROLE_KEY, PersistentDataType.STRING);
        if (roleString != null) {
            try {
                return AttachmentRole.valueOf(roleString);
            } catch (IllegalArgumentException _) {
            }
        }
        return AttachmentRole.SUBORDINATE;
    }

    private void handleDamageDealt(EntityDamageByEntityEvent event) {
        Entity rawDamager = event.getDamager();
        Entity source =
                (rawDamager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter)
                        ? shooter
                        : rawDamager;

        BlightedEntity damager = getBlightedEntity(source);
        if (damager != null) {
            damager.onDamageDealt(event);
        }
    }

    private void handleAttachmentDamage(BlightedEntity owner, Entity attachmentEntity, EntityDamageEvent event) {
        LivingEntity ownerEntity = owner.getEntity();
        if (ownerEntity == null || ownerEntity.isDead()) {
            attachmentEntity.remove();
            EntityManager.removeAttachmentOwner(attachmentEntity.getUniqueId());
            return;
        }

        event.setCancelled(true);
        Entity realDamager = getRealDamager(event);

        flashHurtAndCancelKnockback(owner, attachmentEntity);

        if (attachmentEntity instanceof LivingEntity livingAttachment) {
            syncEquipment(livingAttachment, ownerEntity);
        }

        ownerEntity.damage(event.getFinalDamage(), realDamager);
    }

    private void handleBlightedEntityDamage(BlightedEntity blighted, LivingEntity entity, EntityDamageEvent event) {
        Entity realDamager = getRealDamager(event);
        if (blighted.shouldBlockSameTickDamage(realDamager)) {
            event.setCancelled(true);
            return;
        }

        flashHurtAndCancelKnockback(blighted, entity);
        if (handleImmunity(blighted, entity, event)) {
            return;
        }

        handleResistance(blighted, entity, event);

        blighted.onDamageTaken(event);
        for (var component : blighted.getComponents()) {
            component.onDamageTaken(blighted, event);
        }
        double remainingHealth = entity.getHealth() - event.getFinalDamage();

        if (remainingHealth > 0) {
            Bukkit.getScheduler()
                    .runTaskLater(
                            BlightedSMP.getInstance(),
                            () -> {
                                if (entity.isValid() && !entity.isDead()) {
                                    blighted.updateBossBar();
                                    blighted.evaluatePhases(entity.getHealth());
                                }
                            },
                            1L);
            return;
        }
        blighted.killAllAttachments();
    }

    private void flashHurtAndCancelKnockback(BlightedEntity owner, Entity hitEntity) {
        if (hitEntity instanceof LivingEntity livingHit) {
            livingHit.playHurtAnimation(0.0f);
            livingHit.setVelocity(new Vector(0, 0, 0));
        }

        LivingEntity ownerEntity = owner.getEntity();
        if (ownerEntity != null && !ownerEntity.equals(hitEntity)) {
            ownerEntity.playHurtAnimation(0.0f);
        }

        for (EntityAttachment attachment : owner.attachments) {
            if (attachment.role() == AttachmentRole.SUBORDINATE) {
                continue;
            }
            Entity sibling = attachment.entity();
            if (sibling instanceof LivingEntity livingSibling && !sibling.equals(hitEntity)) {
                livingSibling.playHurtAnimation(0.0f);
                livingSibling.setVelocity(new Vector(0, 0, 0));
            }
        }
    }

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        Entity target = event.getEntity();
        if (!target.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        BlightedEntity owner = EntityManager.getAttachmentOwner(target.getUniqueId());
        if (owner == null) return;

        LivingEntity ownerEntity = owner.getEntity();
        if (ownerEntity == null || ownerEntity.isDead()) return;

        event.setCancelled(true);
        if (event.getNewEffect() != null) {
            ownerEntity.addPotionEffect(event.getNewEffect());
        }
    }

    private boolean handleImmunity(BlightedEntity blighted, LivingEntity entity, EntityDamageEvent event) {
        EntityImmunity triggered = blighted.getTriggeredImmunity(entity, event);
        if (triggered == null) return false;

        event.setCancelled(true);

        Player player = getPlayerDamager(getRealDamager(event));
        if (player != null) {
            player.sendMessage(triggered.getImmunityMessage());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 0.6f);
        }
        return true;
    }

    private void handleResistance(BlightedEntity blighted, LivingEntity entity, EntityDamageEvent event) {
        double resistancePercent = blighted.getResistancePercent(entity, event);
        if (resistancePercent <= 0.0) return;

        double multiplier = Math.max(0.0, 1.0 - (resistancePercent / 100.0));
        event.setDamage(event.getDamage() * multiplier);
    }

    @EventHandler
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        UUID entityId = entity.getUniqueId();
        BlightedEntity blighted = EntityManager.getDirectBlightedEntity(entityId);
        if (blighted != null) {
            blighted.updateBossBar();
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        if (!dead.getScoreboardTags().contains(FAST_PASS_TAG)) return;
        UUID uuid = dead.getUniqueId();

        BlightedEntity attachmentOwner = EntityManager.getAttachmentOwner(uuid);
        if (attachmentOwner != null) {
            EntityManager.removeAttachmentOwner(uuid);
        }

        boolean isAttachment = attachmentOwner != null
                || dead.getPersistentDataContainer().has(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING);

        if (isAttachment) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }

        BlightedEntity blighted = EntityManager.getDirectBlightedEntity(uuid);
        if (blighted == null) return;
        EntityManager.removeDirectBlightedEntity(uuid);

        blighted.cleanup();

        BlightedPlayer killer = dead.getKiller() != null ? BlightedPlayer.get(dead.getKiller()) : null;

        blighted.dropLoot(dead.getLocation(), killer);
        blighted.onDeath(dead.getLocation());

        event.getDrops().clear();
        event.setDroppedExp(blighted.getDroppedExp());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(BlightedSMP.getInstance(), () -> rehydrateChunk(event.getChunk()), 1L);
    }

    public static Collection<BlightedEntity> getActiveEntities() {
        return EntityManager.getActiveEntities();
    }

    public static void rehydrateChunk(Chunk chunk) {
        EntityManager.rehydrateChunk(chunk);
    }

    private Player getPlayerDamager(Entity damager) {
        if (damager instanceof Player player) return player;
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            return shooter;
        }
        return null;
    }

    private Entity getRealDamager(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            return entityDamageByEntityEvent.getDamager();
        }
        return null;
    }

    private static void syncEquipment(LivingEntity target, LivingEntity source) {
        EntityEquipment sourceEquipment = source.getEquipment();
        EntityEquipment targetEquipment = target.getEquipment();
        if (sourceEquipment == null || targetEquipment == null) {
            return;
        }

        targetEquipment.setArmorContents(sourceEquipment.getArmorContents());
        targetEquipment.setItemInMainHand(sourceEquipment.getItemInMainHand());
        targetEquipment.setItemInOffHand(sourceEquipment.getItemInOffHand());
    }
}
