package fr.moussax.blightedMC.engine.entities.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.attachment.AttachmentRole;
import fr.moussax.blightedMC.engine.entities.attachment.EntityAttachment;
import fr.moussax.blightedMC.engine.entities.immunity.EntityImmunity;
import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static fr.moussax.blightedMC.engine.entities.BlightedEntity.ATTACHMENT_OFFSET_X_KEY;
import static fr.moussax.blightedMC.engine.entities.BlightedEntity.ATTACHMENT_OFFSET_Y_KEY;
import static fr.moussax.blightedMC.engine.entities.BlightedEntity.ATTACHMENT_OFFSET_Z_KEY;
import static fr.moussax.blightedMC.engine.entities.BlightedEntity.ATTACHMENT_OWNER_KEY;
import static fr.moussax.blightedMC.engine.entities.BlightedEntity.ATTACHMENT_ROLE_KEY;
import static fr.moussax.blightedMC.engine.entities.BlightedEntity.ATTACHMENT_SYNC_PITCH_KEY;
import static fr.moussax.blightedMC.engine.entities.BlightedEntity.ATTACHMENT_SYNC_YAW_KEY;
import static fr.moussax.blightedMC.engine.entities.BlightedEntity.ENTITY_ID_KEY;
import static fr.moussax.blightedMC.engine.entities.BlightedEntity.FAST_PASS_TAG;

public final class BlightedEntitiesListener implements Listener {

    private static final Map<UUID, BlightedEntity> BLIGHTED_ENTITIES = new ConcurrentHashMap<>();
    private static final Map<UUID, BlightedEntity> ATTACHMENT_OWNERS = new ConcurrentHashMap<>();
    private final Set<UUID> processingDamageIds = ConcurrentHashMap.newKeySet();

    private static final long ORPHAN_SWEEP_PERIOD_TICKS = 100L; // 5s

    public BlightedEntitiesListener() {
        Bukkit.getScheduler().runTaskTimer(
                BlightedMC.getInstance(), BlightedEntitiesListener::sweepOrphanedEntities,
                ORPHAN_SWEEP_PERIOD_TICKS, ORPHAN_SWEEP_PERIOD_TICKS
        );
    }

    private static void sweepOrphanedEntities() {
        for (BlightedEntity blighted : List.copyOf(BLIGHTED_ENTITIES.values())) {
            LivingEntity entity = blighted.getEntity();
            if (entity == null || entity.isDead() || !entity.isValid()) {
                if (entity != null) {
                    BLIGHTED_ENTITIES.remove(entity.getUniqueId(), blighted);
                } else {
                    BLIGHTED_ENTITIES.values().remove(blighted);
                }
                blighted.cleanup();
            }
        }
    }

    public static void registerEntity(LivingEntity entity, BlightedEntity blighted) {
        if (entity == null || blighted == null) return;
        BLIGHTED_ENTITIES.put(entity.getUniqueId(), blighted);
    }

    public static void unregisterEntity(LivingEntity entity) {
        if (entity == null) return;
        BLIGHTED_ENTITIES.remove(entity.getUniqueId());
    }

    public static void registerAttachment(Entity attachment, BlightedEntity owner) {
        if (attachment == null || owner == null) return;
        ATTACHMENT_OWNERS.put(attachment.getUniqueId(), owner);
    }

    public static void unregisterAttachment(Entity attachment) {
        if (attachment == null) return;
        ATTACHMENT_OWNERS.remove(attachment.getUniqueId());
    }

    public static BlightedEntity getBlightedEntity(Entity entity) {
        if (entity == null) return null;
        UUID id = entity.getUniqueId();
        BlightedEntity blighted = BLIGHTED_ENTITIES.get(id);
        return blighted != null ? blighted : ATTACHMENT_OWNERS.get(id);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent damageByEntity) {
            handleDamageDealt(damageByEntity);
        }

        Entity target = event.getEntity();
        if (!target.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        UUID entityId = target.getUniqueId();
        if (processingDamageIds.contains(entityId)) return;

        processingDamageIds.add(entityId);
        try {
            BlightedEntity owner = ATTACHMENT_OWNERS.get(entityId);
            if (owner != null) {
                LivingEntity ownerEntity = owner.getEntity();
                if (ownerEntity == null || ownerEntity.isDead()) {
                    target.remove();
                    ATTACHMENT_OWNERS.remove(entityId);
                    return;
                }

                AttachmentRole role = getAttachmentRole(owner, target);
                if (role == AttachmentRole.HITBOX) {
                    handleAttachmentDamage(owner, target, event);
                    return;
                }
            }

            if (target instanceof LivingEntity living) {
                BlightedEntity blighted = BLIGHTED_ENTITIES.get(entityId);
                if (blighted != null) {
                    handleBlightedEntityDamage(blighted, living, event);
                }
            }
        } finally {
            processingDamageIds.remove(entityId);
        }
    }

    private AttachmentRole getAttachmentRole(BlightedEntity owner, Entity attachmentEntity) {
        if (owner != null && owner.attachments != null) {
            for (EntityAttachment attachment : owner.attachments) {
                if (attachment.entity() != null && attachment.entity().equals(attachmentEntity)) {
                    return attachment.role();
                }
            }
        }

        if (attachmentEntity != null) {
            PersistentDataContainer pdc = attachmentEntity.getPersistentDataContainer();
            if (pdc.has(ATTACHMENT_ROLE_KEY, PersistentDataType.STRING)) {
                String roleStr = pdc.get(ATTACHMENT_ROLE_KEY, PersistentDataType.STRING);
                if (roleStr != null) {
                    try {
                        return AttachmentRole.valueOf(roleStr);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }
        return AttachmentRole.SUBORDINATE;
    }

    private void handleDamageDealt(EntityDamageByEntityEvent event) {
        Entity rawDamager = event.getDamager();
        Entity source = (rawDamager instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter)
                ? shooter
                : rawDamager;

        BlightedEntity damager = getBlightedEntity(source);
        if (damager != null) {
            damager.onDamageDealt(event);
        }
    }

    private void handleAttachmentDamage(
            BlightedEntity owner,
            Entity attachmentEntity,
            EntityDamageEvent event
    ) {
        LivingEntity ownerEntity = owner.getEntity();
        if (ownerEntity == null || ownerEntity.isDead()) {
            attachmentEntity.remove();
            ATTACHMENT_OWNERS.remove(attachmentEntity.getUniqueId());
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
            Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> {
                if (entity.isValid() && !entity.isDead()) {
                    blighted.updateBossBar();
                    blighted.evaluatePhases(entity.getHealth());
                }
            }, 1L);
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

        BlightedEntity owner = ATTACHMENT_OWNERS.get(target.getUniqueId());
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
        BlightedEntity blighted = BLIGHTED_ENTITIES.get(entityId);
        if (blighted != null) {
            blighted.updateBossBar();
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        if (!dead.getScoreboardTags().contains(FAST_PASS_TAG)) return;
        UUID uuid = dead.getUniqueId();

        boolean isAttachment = ATTACHMENT_OWNERS.remove(uuid) != null
                || dead.getPersistentDataContainer().has(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING);

        if (isAttachment) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }

        BlightedEntity blighted = BLIGHTED_ENTITIES.remove(uuid);
        if (blighted == null) return;

        blighted.cleanup();

        BlightedPlayer killer = dead.getKiller() != null
                ? BlightedPlayer.getBlightedPlayer(dead.getKiller())
                : null;

        blighted.dropLoot(dead.getLocation(), killer);
        blighted.onDeath(dead.getLocation());

        event.getDrops().clear();
        event.setDroppedExp(blighted.getDroppedExp());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(
                BlightedMC.getInstance(), () -> rehydrateChunk(event.getChunk()), 1L
        );
    }

    public static Collection<BlightedEntity> getActiveEntities() {
        return List.copyOf(BLIGHTED_ENTITIES.values());
    }

    public static void rehydrateChunk(Chunk chunk) {
        Entity[] entities = chunk.getEntities();

        // Pass 1: rehydrate main blighted entities.
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.getScoreboardTags().contains(FAST_PASS_TAG)) continue;

            PersistentDataContainer pdc = living.getPersistentDataContainer();
            if (!pdc.has(ENTITY_ID_KEY, PersistentDataType.STRING)) continue;

            BlightedEntity existing = BLIGHTED_ENTITIES.get(living.getUniqueId());
            if (existing != null) {
                if (existing.getEntity() != living) {
                    existing.attachToExisting(living);
                }
                continue;
            }

            String entityId = pdc.get(ENTITY_ID_KEY, PersistentDataType.STRING);
            BlightedEntity prototype = EntitiesRegistry.get(entityId);
            if (prototype == null) continue;

            prototype.clone().attachToExisting(living);
        }

        // Pass 2: re-register attachment entities carrying ATTACHMENT_OWNER_KEY.
        for (Entity entity : entities) {
            if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) continue;

            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            if (!pdc.has(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING)) continue;

            String ownerUuidStr = pdc.get(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING);
            String roleStr = pdc.get(ATTACHMENT_ROLE_KEY, PersistentDataType.STRING);
            if (ownerUuidStr == null || roleStr == null) continue;

            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(ownerUuidStr);
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            BlightedEntity owner = BLIGHTED_ENTITIES.get(ownerUuid);
            if (owner == null) continue;

            AttachmentRole role;
            try {
                role = AttachmentRole.valueOf(roleStr);
            } catch (IllegalArgumentException ignored) {
                role = AttachmentRole.SUBORDINATE;
            }

            Double offsetX = pdc.get(ATTACHMENT_OFFSET_X_KEY, PersistentDataType.DOUBLE);
            Double offsetY = pdc.get(ATTACHMENT_OFFSET_Y_KEY, PersistentDataType.DOUBLE);
            Double offsetZ = pdc.get(ATTACHMENT_OFFSET_Z_KEY, PersistentDataType.DOUBLE);
            Vector offset = new Vector(
                    offsetX != null ? offsetX : 0.0,
                    offsetY != null ? offsetY : 0.0,
                    offsetZ != null ? offsetZ : 0.0
            );

            Byte yawByte = pdc.get(ATTACHMENT_SYNC_YAW_KEY, PersistentDataType.BYTE);
            Byte pitchByte = pdc.get(ATTACHMENT_SYNC_PITCH_KEY, PersistentDataType.BYTE);
            boolean syncYaw = yawByte == null || yawByte == 1;
            boolean syncPitch = pitchByte != null && pitchByte == 1;

            owner.attachments.removeIf(a -> a.entity() != null && a.entity().getUniqueId().equals(entity.getUniqueId()));
            owner.attachments.add(new EntityAttachment(entity, role, offset, syncYaw, syncPitch));
            registerAttachment(entity, owner);
        }

        // Pass 3: Purge orphan attachments whose owner entity no longer exists.
        Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> {
            if (!chunk.isLoaded()) return;
            for (Entity entity : chunk.getEntities()) {
                if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) continue;
                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                if (!pdc.has(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING)) continue;

                String ownerUuidString = pdc.get(ATTACHMENT_OWNER_KEY, PersistentDataType.STRING);
                if (ownerUuidString == null) continue;

                try {
                    UUID ownerUuid = UUID.fromString(ownerUuidString);
                    BlightedEntity owner = BLIGHTED_ENTITIES.get(ownerUuid);
                    if (owner == null || owner.getEntity() == null || !owner.getEntity().isValid()) {
                        entity.remove();
                        ATTACHMENT_OWNERS.remove(entity.getUniqueId());
                    }
                } catch (IllegalArgumentException ignored) {
                    entity.remove();
                }
            }
        }, 3L);
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
