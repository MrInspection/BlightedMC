package fr.moussax.blightedMC.engine.entities.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.engine.entities.attachment.AttachmentRole;
import fr.moussax.blightedMC.engine.entities.attachment.EntityAttachment;
import fr.moussax.blightedMC.engine.entities.immunity.EntityImmunity;
import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static fr.moussax.blightedMC.engine.entities.AbstractBlightedEntity.ENTITY_ID_KEY;
import static fr.moussax.blightedMC.engine.entities.AbstractBlightedEntity.FAST_PASS_TAG;

public final class BlightedEntitiesListener implements Listener {

    private static final Map<UUID, AbstractBlightedEntity> BLIGHTED_ENTITIES = new ConcurrentHashMap<>();
    private static final Map<UUID, AbstractBlightedEntity> ATTACHMENT_OWNERS = new ConcurrentHashMap<>();
    private final ThreadLocal<Set<UUID>> processingDamageIds = ThreadLocal.withInitial(HashSet::new);

    public static void registerEntity(LivingEntity entity, AbstractBlightedEntity blighted) {
        if (entity == null || blighted == null) return;
        BLIGHTED_ENTITIES.put(entity.getUniqueId(), blighted);
    }

    public static void unregisterEntity(LivingEntity entity) {
        if (entity == null) return;
        BLIGHTED_ENTITIES.remove(entity.getUniqueId());
    }

    public static void registerAttachment(Entity attachment, AbstractBlightedEntity owner) {
        if (attachment == null || owner == null) return;
        ATTACHMENT_OWNERS.put(attachment.getUniqueId(), owner);
    }

    public static void unregisterAttachment(Entity attachment) {
        if (attachment == null) return;
        ATTACHMENT_OWNERS.remove(attachment.getUniqueId());
    }

    public static AbstractBlightedEntity getBlightedEntity(Entity entity) {
        if (entity == null) return null;
        UUID id = entity.getUniqueId();
        AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.get(id);
        return blighted != null ? blighted : ATTACHMENT_OWNERS.get(id);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        Set<UUID> processing = processingDamageIds.get();
        UUID entityId = entity.getUniqueId();
        if (processing.contains(entityId)) return;

        processing.add(entityId);
        try {
            AbstractBlightedEntity owner = ATTACHMENT_OWNERS.get(entityId);
            if (owner != null) {
                handleAttachmentDamage(owner, entity, event);
                return;
            }

            AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.get(entityId);
            if (blighted != null) {
                handleBlightedEntityDamage(blighted, entity, event);
            }
        } finally {
            processing.remove(entityId);
        }
    }

    private void handleAttachmentDamage(
        AbstractBlightedEntity owner,
        LivingEntity attachmentEntity,
        EntityDamageEvent event
    ) {
        LivingEntity ownerEntity = owner.getEntity();
        if (ownerEntity == null || ownerEntity.isDead()) {
            attachmentEntity.remove();
            ATTACHMENT_OWNERS.remove(attachmentEntity.getUniqueId());
            return;
        }

        AttachmentRole role = resolveAttachmentRole(owner, attachmentEntity);

        if (role == AttachmentRole.BODY) {
            event.setCancelled(true);
            ownerEntity.damage(event.getFinalDamage(), getRealDamager(event));
            syncEquipment(attachmentEntity, ownerEntity);
        }
    }

    private void handleBlightedEntityDamage(
        AbstractBlightedEntity blighted,
        LivingEntity entity,
        EntityDamageEvent event
    ) {
        forwardDamageToBodyAttachments(blighted, event);

        if (event instanceof EntityDamageByEntityEvent damageByEntity) {
            if (handleImmunity(blighted, entity, damageByEntity)) return;
        }

        blighted.onDamageTaken(event);

        double remainingHealth = entity.getHealth() - event.getFinalDamage();

        if (remainingHealth > 0) {
            Bukkit.getScheduler().runTaskLater(
                BlightedMC.getInstance(),
                blighted::updateBossBar,
                1L
            );
            return;
        }

        // Prevent death while any BODY attachment is still alive
        if (blighted.hasLivingBodyAttachment()) {
            event.setCancelled(true);
            entity.setHealth(1.0);
            blighted.updateBossBar();
            return;
        }

        blighted.killAllAttachments();
    }

    private void forwardDamageToBodyAttachments(AbstractBlightedEntity blighted, EntityDamageEvent event) {
        if (blighted.attachments.isEmpty()) return;

        for (EntityAttachment attachment : new ArrayList<>(blighted.attachments)) {
            if (attachment.role() != AttachmentRole.BODY) continue;
            if (!(attachment.entity() instanceof LivingEntity living) || living.isDead()) continue;

            living.damage(event.getFinalDamage(), getRealDamager(event));
            syncEquipment(living, blighted.getEntity());
        }
    }

    private boolean handleImmunity(
        AbstractBlightedEntity blighted,
        LivingEntity entity,
        EntityDamageByEntityEvent event
    ) {
        EntityImmunity triggered = blighted.getTriggeredImmunity(entity, event);
        if (triggered == null) return false;

        event.setCancelled(true);

        Player player = getPlayerDamager(event.getDamager());
        if (player != null) {
            player.sendMessage(triggered.getImmunityMessage());
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 0.6f);
        }
        return true;
    }

    @EventHandler
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        UUID id = entity.getUniqueId();

        AbstractBlightedEntity owner = ATTACHMENT_OWNERS.get(id);
        if (owner != null) {
            // BODY attachment healed — sync health to owner
            if (resolveAttachmentRole(owner, entity) == AttachmentRole.BODY) {
                double newHealth = clampedHealth(entity, event.getAmount());
                owner.getEntity().setHealth(newHealth);
                entity.setHealth(newHealth);
                owner.updateBossBar();
            }
            return;
        }

        AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.get(id);
        if (blighted == null) return;

        blighted.updateBossBar();

        // Sync heal to BODY attachments
        if (!blighted.attachments.isEmpty()) {
            double newHealth = clampedHealth(entity, event.getAmount());
            for (EntityAttachment attachment : blighted.attachments) {
                if (attachment.role() != AttachmentRole.BODY) continue;
                if (attachment.entity() instanceof LivingEntity living && !living.isDead()) {
                    living.setHealth(newHealth);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        if (!dead.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        UUID uuid = dead.getUniqueId();

        AbstractBlightedEntity owner = ATTACHMENT_OWNERS.remove(uuid);
        if (owner != null) {
            handleAttachmentDeath(owner, dead, event);
            return;
        }

        AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.remove(uuid);
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

    private void handleAttachmentDeath(
        AbstractBlightedEntity owner,
        LivingEntity deadAttachment,
        EntityDeathEvent event
    ) {
        event.getDrops().clear();
        event.setDroppedExp(0);

        AttachmentRole role = resolveAttachmentRole(owner, deadAttachment);

        if (role == AttachmentRole.BODY) {
            LivingEntity ownerEntity = owner.getEntity();
            if (ownerEntity != null && !ownerEntity.isDead()) {
                ownerEntity.setHealth(0);
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> rehydrateChunk(event.getChunk()), 1L);
    }

    public static void rehydrateChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.getScoreboardTags().contains(FAST_PASS_TAG)) continue;
            if (BLIGHTED_ENTITIES.containsKey(living.getUniqueId())) continue;

            PersistentDataContainer pdc = living.getPersistentDataContainer();
            if (!pdc.has(ENTITY_ID_KEY, PersistentDataType.STRING)) continue;

            String entityId = pdc.get(ENTITY_ID_KEY, PersistentDataType.STRING);

            AbstractBlightedEntity prototype = EntitiesRegistry.get(entityId);
            if (prototype == null) continue;

            prototype.clone().attachToExisting(living);
        }
    }

    private AttachmentRole resolveAttachmentRole(AbstractBlightedEntity owner, LivingEntity attachmentEntity) {
        UUID targetId = attachmentEntity.getUniqueId();
        for (EntityAttachment attachment : owner.attachments) {
            if (attachment.entity() != null
                && attachment.entity().getUniqueId().equals(targetId)) {
                return attachment.role();
            }
        }
        return AttachmentRole.DEPENDENT;
    }

    private double clampedHealth(LivingEntity entity, double healAmount) {
        double maxHealth = Objects.requireNonNull(
            entity.getAttribute(Attribute.MAX_HEALTH)
        ).getValue();
        return Math.min(entity.getHealth() + healAmount, maxHealth);
    }

    private Player getPlayerDamager(Entity damager) {
        if (damager instanceof Player player) return player;
        if (damager instanceof Projectile projectile
            && projectile.getShooter() instanceof Player shooter) {
            return shooter;
        }
        return null;
    }

    private Entity getRealDamager(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent e) return e.getDamager();
        return null;
    }

    private static void syncEquipment(LivingEntity target, LivingEntity source) {
        EntityEquipment sourceEquipment = source.getEquipment();
        EntityEquipment targetEquipment = target.getEquipment();
        if (sourceEquipment == null || targetEquipment == null) return;

        targetEquipment.setArmorContents(sourceEquipment.getArmorContents());
        targetEquipment.setItemInMainHand(sourceEquipment.getItemInMainHand());
        targetEquipment.setItemInOffHand(sourceEquipment.getItemInOffHand());
    }
}
