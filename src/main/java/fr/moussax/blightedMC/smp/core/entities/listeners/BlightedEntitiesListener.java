package fr.moussax.blightedMC.smp.core.entities.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity;
import fr.moussax.blightedMC.smp.core.entities.EntityAttachment;
import fr.moussax.blightedMC.smp.core.entities.immunity.EntityImmunity;
import fr.moussax.blightedMC.smp.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
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

import static fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity.ENTITY_ID_KEY;
import static fr.moussax.blightedMC.smp.core.entities.AbstractBlightedEntity.FAST_PASS_TAG;

public class BlightedEntitiesListener implements Listener {

    private static final Map<UUID, AbstractBlightedEntity> BLIGHTED_ENTITIES = new HashMap<>();
    private static final Map<UUID, AbstractBlightedEntity> ATTACHMENT_OWNERS = new HashMap<>();
    private final ThreadLocal<Set<UUID>> processingDamageEntityIds = ThreadLocal.withInitial(HashSet::new);

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
        if (blighted != null) return blighted;

        return ATTACHMENT_OWNERS.get(id);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        Set<UUID> processing = processingDamageEntityIds.get();
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

    private void handleAttachmentDamage(AbstractBlightedEntity owner, LivingEntity attachmentEntity, EntityDamageEvent event) {
        LivingEntity ownerEntity = owner.getEntity();
        if (ownerEntity == null || ownerEntity.isDead()) {
            attachmentEntity.remove();
            ATTACHMENT_OWNERS.remove(attachmentEntity.getUniqueId());
            return;
        }

        ownerEntity.damage(event.getFinalDamage(), getRealDamager(event));
        syncArmor(attachmentEntity, ownerEntity);
    }

    private void handleBlightedEntityDamage(AbstractBlightedEntity blighted, LivingEntity entity, EntityDamageEvent event) {
        forwardDamageToAttachments(blighted, event);

        if (event instanceof EntityDamageByEntityEvent damageByEntity) {
            if (handleImmunity(blighted, entity, damageByEntity)) return;
        }

        blighted.onDamageTaken(event);

        double remainingHealth = entity.getHealth() - event.getFinalDamage();

        if (remainingHealth > 0) {
            Bukkit.getScheduler().runTaskLater(
                BlightedMC.getInstance(),
                blighted::updateNameTag,
                1L
            );
            return;
        }

        if (shouldPreventDeath(blighted)) {
            event.setCancelled(true);
            entity.setHealth(1.0);
            blighted.updateNameTag();
            return;
        }

        blighted.killAllAttachments();
    }

    private void forwardDamageToAttachments(AbstractBlightedEntity blighted, EntityDamageEvent event) {
        if (blighted.attachments.isEmpty()) return;

        for (EntityAttachment attachment : new ArrayList<>(blighted.attachments)) {
            if (!(attachment.entity() instanceof LivingEntity living) || living.isDead()) continue;

            living.damage(event.getFinalDamage(), getRealDamager(event));
            syncArmor(living, blighted.getEntity());
        }
    }

    private boolean handleImmunity(
        AbstractBlightedEntity blighted,
        LivingEntity entity,
        EntityDamageByEntityEvent event
    ) {
        EntityImmunity triggeredRule = blighted.getTriggeredImmunity(entity, event);
        if (triggeredRule == null) return false;

        event.setCancelled(true);

        Player player = getPlayerDamager(event.getDamager());
        if (player != null) {
            player.sendMessage(triggeredRule.getImmunityMessage());
            player.playSound(
                player.getLocation(),
                Sound.ENTITY_ENDERMAN_TELEPORT,
                100,
                0.6f
            );
        }

        return true;
    }

    private boolean shouldPreventDeath(AbstractBlightedEntity blighted) {
        if (blighted.attachments.isEmpty()) return false;

        return blighted.attachments.stream()
            .anyMatch(att ->
                att.entity() instanceof LivingEntity living && !living.isDead()
            );
    }

    @EventHandler
    public void onEntityHeal(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!entity.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        UUID id = entity.getUniqueId();
        AbstractBlightedEntity owner = ATTACHMENT_OWNERS.get(id);
        if (owner != null) {
            double newHealth = calculateNewHealth(entity, event.getAmount());
            owner.getEntity().setHealth(newHealth);
            entity.setHealth(newHealth);
            owner.updateNameTag();
            return;
        }

        AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.get(id);
        if (blighted == null) return;

        blighted.updateNameTag();
        if (blighted.attachments.isEmpty()) return;

        double newHealth = calculateNewHealth(entity, event.getAmount());
        for (EntityAttachment attachment : blighted.attachments) {
            if (attachment.entity() instanceof LivingEntity living && !living.isDead()) {
                living.setHealth(newHealth);
            }
        }
    }

    private double calculateNewHealth(LivingEntity entity, double healAmount) {
        double maxHealth = Objects.requireNonNull(
            entity.getAttribute(Attribute.MAX_HEALTH)
        ).getValue();

        return Math.min(entity.getHealth() + healAmount, maxHealth);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        if (!dead.getScoreboardTags().contains(FAST_PASS_TAG)) return;

        UUID uuid = dead.getUniqueId();

        AbstractBlightedEntity owner = ATTACHMENT_OWNERS.remove(uuid);
        if (owner != null) {
            LivingEntity ownerEntity = owner.getEntity();
            if (ownerEntity != null && !ownerEntity.isDead()) {
                ownerEntity.setHealth(0);
            }

            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }

        AbstractBlightedEntity blighted = BLIGHTED_ENTITIES.remove(uuid);
        if (blighted == null) return;

        blighted.cleanup();

        BlightedPlayer player = dead.getKiller() != null
            ? BlightedPlayer.getBlightedPlayer(dead.getKiller())
            : null;

        blighted.dropLoot(dead.getLocation(), player);
        blighted.onDeath(dead.getLocation());

        event.getDrops().clear();
        event.setDroppedExp(blighted.getDroppedExp());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskLater(
            BlightedMC.getInstance(),
            () -> rehydrateChunk(event.getChunk()),
            1L
        );
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
            if (prototype == null || !prototype.isPersistent()) continue;

            AbstractBlightedEntity instance = prototype.clone();
            instance.attachToExisting(living);
        }
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
        if (event instanceof EntityDamageByEntityEvent e) {
            return e.getDamager();
        }
        return null;
    }

    private static void syncArmor(LivingEntity target, LivingEntity source) {
        EntityEquipment sourceEquipment = source.getEquipment();
        EntityEquipment targetEquipment = target.getEquipment();
        if (sourceEquipment == null || targetEquipment == null) return;

        targetEquipment.setArmorContents(sourceEquipment.getArmorContents());
        targetEquipment.setItemInMainHand(sourceEquipment.getItemInMainHand());
        targetEquipment.setItemInOffHand(sourceEquipment.getItemInOffHand());
    }
}
